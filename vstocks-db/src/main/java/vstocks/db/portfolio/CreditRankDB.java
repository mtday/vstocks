package vstocks.db.portfolio;

import vstocks.db.*;
import vstocks.model.DeltaInterval;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditRank;
import vstocks.model.portfolio.CreditRankCollection;
import vstocks.model.portfolio.RankedUser;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Delta.getReverseDeltas;
import static vstocks.model.SortDirection.DESC;

class CreditRankDB extends BaseDB {
    private static final String BATCH_SEQUENCE = "credit_ranks_batch_sequence";

    private static final RowMapper<CreditRank> ROW_MAPPER = rs ->
            new CreditRank()
                    .setBatch(rs.getLong("batch"))
                    .setUserId(rs.getString("user_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setRank(rs.getLong("rank"))
                    .setValue(rs.getLong("value"));

    private static final RowMapper<RankedUser> USER_ROW_MAPPER = rs ->
            new RankedUser()
                    .setUser(UserDB.ROW_MAPPER.map(rs))
                    .setBatch(rs.getLong("batch"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                    .setRank(rs.getLong("rank"))
                    .setValue(rs.getLong("value"));

    private static final RowSetter<CreditRank> INSERT_ROW_SETTER = (ps, creditRank) -> {
        int index = 0;
        ps.setLong(++index, creditRank.getBatch());
        ps.setString(++index, creditRank.getUserId());
        ps.setTimestamp(++index, Timestamp.from(creditRank.getTimestamp()));
        ps.setLong(++index, creditRank.getRank());
        ps.setLong(++index, creditRank.getValue());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return asList(BATCH.toSort(DESC), RANK.toSort(), USER_ID.toSort());
    }

    public long setCurrentBatch(Connection connection, long batch) {
        return setSequenceValue(connection, BATCH_SEQUENCE, batch);
    }

    public int generate(Connection connection) {
        long batch = getNextSequenceValue(connection, BATCH_SEQUENCE);
        String sql = "INSERT INTO credit_ranks (batch, user_id, timestamp, rank, value) "
                + "(SELECT ? AS batch, user_id, NOW(), RANK() OVER (ORDER BY credits DESC), credits AS value "
                + "FROM user_credits ORDER BY credits DESC)";
        return update(connection, sql, batch);
    }

    public CreditRankCollection getLatest(Connection connection, String userId) {
        Instant earliest = DeltaInterval.getLast().getEarliest();

        String sql = "SELECT * FROM credit_ranks WHERE timestamp >= ? AND user_id = ? ORDER BY timestamp DESC";
        List<CreditRank> ranks = new ArrayList<>();
        consume(connection, ROW_MAPPER, ranks::add, sql, earliest, userId);

        // Make sure the most recent rank has an up-to-date timestamp and value so that the generated deltas have
        // up-to-date values.
        if (!ranks.isEmpty()) {
            CreditRank latest = ranks.iterator().next();
            String latestSql = "SELECT ? AS batch, user_id, NOW() AS timestamp, ? AS rank, credits AS value "
                    + "FROM user_credits WHERE user_id = ?";
            getOne(connection, ROW_MAPPER, latestSql, latest.getBatch() + 1, latest.getRank(), userId)
                    .ifPresent(rank -> ranks.add(0, rank));
        }

        return new CreditRankCollection()
                .setRanks(ranks)
                .setDeltas(getReverseDeltas(ranks, CreditRank::getTimestamp, CreditRank::getRank));
    }

    public Results<CreditRank> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM credit_ranks %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM credit_ranks";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public Results<RankedUser> getUsers(Connection connection, Page page) {
        long batch = getCurrentSequenceValue(connection, BATCH_SEQUENCE);
        String sql = "SELECT * FROM credit_ranks r JOIN users u ON (u.id = r.user_id) WHERE batch = ? "
                + "ORDER BY r.rank, u.username LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) FROM credit_ranks WHERE batch = ?";
        return results(connection, USER_ROW_MAPPER, page, sql, count, batch);
    }

    public int add(Connection connection, CreditRank creditRank) {
        String sql = "INSERT INTO credit_ranks (batch, user_id, timestamp, rank, value) VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT credit_ranks_pk "
                + "DO UPDATE SET rank = EXCLUDED.rank, value = EXCLUDED.value "
                + "WHERE credit_ranks.rank != EXCLUDED.rank OR credit_ranks.value != EXCLUDED.value";
        return update(connection, INSERT_ROW_SETTER, sql, creditRank);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM credit_ranks WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM credit_ranks");
    }
}
