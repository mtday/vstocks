package vstocks.db.portfolio;

import vstocks.db.jdbc.table.BaseTable;
import vstocks.db.jdbc.table.RowMapper;
import vstocks.db.jdbc.table.RowSetter;
import vstocks.model.*;
import vstocks.model.portfolio.CreditRank;
import vstocks.model.portfolio.CreditRankCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static vstocks.model.DatabaseField.RANK;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.SortDirection.DESC;

class CreditRankTable extends BaseTable {
    private static final RowMapper<CreditRank> ROW_MAPPER = rs ->
            new CreditRank()
                    .setUserId(rs.getString("user_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setRank(rs.getLong("rank"));

    private static final RowSetter<CreditRank> INSERT_ROW_SETTER = (ps, creditRank) -> {
        int index = 0;
        ps.setString(++index, creditRank.getUserId());
        ps.setTimestamp(++index, Timestamp.from(creditRank.getTimestamp()));
        ps.setLong(++index, creditRank.getRank());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort()));
    }

    public int generate(Connection connection, Consumer<CreditRank> consumer) {
        String sql = "SELECT user_id, NOW() AS timestamp, ROW_NUMBER() OVER (ORDER BY credits DESC) AS rank "
                + "FROM user_credits ORDER BY credits DESC";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public CreditRankCollection getLatest(Connection connection, String userId) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        String sql = "SELECT * FROM credit_ranks WHERE timestamp >= ? AND user_id = ? ORDER BY timestamp DESC";
        List<CreditRank> ranks = new ArrayList<>();
        consume(connection, ROW_MAPPER, ranks::add, sql, earliest, userId);

        return new CreditRankCollection()
                .setRanks(ranks)
                .setDeltas(Delta.getDeltas(ranks, CreditRank::getTimestamp, r -> -r.getRank()));
    }

    public Results<CreditRank> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM credit_ranks %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM credit_ranks";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, CreditRank creditRank) {
        return addAll(connection, singleton(creditRank));
    }

    public int addAll(Connection connection, Collection<CreditRank> creditRanks) {
        String sql = "INSERT INTO credit_ranks (user_id, timestamp, rank) VALUES (?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT credit_ranks_pk "
                + "DO UPDATE SET rank = EXCLUDED.rank WHERE credit_ranks.rank != EXCLUDED.rank";
        return updateBatch(connection, INSERT_ROW_SETTER, sql, creditRanks);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM credit_ranks WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM credit_ranks");
    }
}
