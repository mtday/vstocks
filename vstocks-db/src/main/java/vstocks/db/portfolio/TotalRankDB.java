package vstocks.db.portfolio;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.db.UserDB;
import vstocks.model.*;
import vstocks.model.portfolio.RankedUser;
import vstocks.model.portfolio.TotalRank;
import vstocks.model.portfolio.TotalRankCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.SortDirection.DESC;

class TotalRankDB extends BaseDB {
    private static final String BATCH_SEQUENCE = "total_ranks_batch_sequence";

    private static final RowMapper<TotalRank> ROW_MAPPER = rs ->
            new TotalRank()
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

    private static final RowSetter<TotalRank> INSERT_ROW_SETTER = (ps, totalRank) -> {
        int index = 0;
        ps.setLong(++index, totalRank.getBatch());
        ps.setString(++index, totalRank.getUserId());
        ps.setTimestamp(++index, Timestamp.from(totalRank.getTimestamp()));
        ps.setLong(++index, totalRank.getRank());
        ps.setLong(++index, totalRank.getValue());
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
        String sql = "INSERT INTO total_ranks (batch, user_id, timestamp, rank, value)"
                + "(SELECT ? AS batch, user_id, timestamp, RANK() OVER (ORDER BY value DESC) AS rank, value FROM ("
                + "  SELECT user_id, NOW() AS timestamp, SUM(credits) + SUM(stocks) AS value FROM ("
                + "    SELECT user_id, 0 AS credits, SUM(value) AS stocks FROM ("
                + "      SELECT DISTINCT ON (us.user_id, us.market, us.symbol)"
                + "        us.user_id, us.market, us.symbol, us.shares * sp.price AS value"
                + "      FROM user_stocks us"
                + "      JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)"
                + "      ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC"
                + "    ) AS priced_stocks GROUP BY user_id"
                + "    UNION"
                + "    SELECT user_id, credits, 0 AS stocks FROM user_credits"
                + "  ) AS portfolio_values GROUP BY user_id ORDER BY value DESC"
                + ") AS ordered_values)";
        return update(connection, sql, batch);
    }

    public TotalRankCollection getLatest(Connection connection, String userId) {
        Instant earliest = DeltaInterval.getLast().getEarliest();

        String sql = "SELECT * FROM total_ranks WHERE timestamp >= ? AND user_id = ? ORDER BY timestamp DESC";
        List<TotalRank> ranks = new ArrayList<>();
        consume(connection, ROW_MAPPER, ranks::add, sql, earliest, userId);

        return new TotalRankCollection()
                .setRanks(ranks)
                .setDeltas(Delta.getDeltas(ranks, TotalRank::getTimestamp, TotalRank::getRank));
    }

    public Results<TotalRank> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM total_ranks %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM total_ranks";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public Results<RankedUser> getUsers(Connection connection, Page page) {
        long batch = getCurrentSequenceValue(connection, BATCH_SEQUENCE);
        String sql = "SELECT * FROM total_ranks r JOIN users u ON (u.id = r.user_id) WHERE batch = ? "
                + "ORDER BY r.rank, u.username LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) FROM total_ranks WHERE batch = ?";
        return results(connection, USER_ROW_MAPPER, page, sql, count, batch);
    }

    public int add(Connection connection, TotalRank totalRank) {
        String sql = "INSERT INTO total_ranks (batch, user_id, timestamp, rank, value) VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT total_ranks_pk "
                + "DO UPDATE SET rank = EXCLUDED.rank, value = EXCLUDED.value "
                + "WHERE total_ranks.rank != EXCLUDED.rank OR total_ranks.value != EXCLUDED.value";
        return update(connection, INSERT_ROW_SETTER, sql, totalRank);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM total_ranks WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM total_ranks");
    }
}
