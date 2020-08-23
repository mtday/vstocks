package vstocks.db.portfolio;

import vstocks.db.BaseTable;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.portfolio.CreditRank;
import vstocks.model.portfolio.CreditRankCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.RANK;
import static vstocks.model.DatabaseField.USER_ID;

class CreditRankDB extends BaseTable {
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
        return new LinkedHashSet<>(asList(RANK.toSort(), USER_ID.toSort()));
    }

    public int generate(Connection connection) {
        String sql = "INSERT INTO credit_ranks (user_id, timestamp, rank) "
                + "(SELECT user_id, NOW(), RANK() OVER (ORDER BY credits DESC) "
                + "FROM user_credits ORDER BY credits DESC)";
        return update(connection, sql);
    }

    public CreditRankCollection getLatest(Connection connection, String userId) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        String sql = "SELECT * FROM credit_ranks WHERE timestamp >= ? AND user_id = ? ORDER BY timestamp DESC";
        List<CreditRank> ranks = new ArrayList<>();
        consume(connection, ROW_MAPPER, ranks::add, sql, earliest, userId);

        return new CreditRankCollection()
                .setRanks(ranks)
                .setDeltas(Delta.getDeltas(ranks, CreditRank::getTimestamp, CreditRank::getRank));
    }

    public Results<CreditRank> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM credit_ranks %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM credit_ranks";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, CreditRank creditRank) {
        String sql = "INSERT INTO credit_ranks (user_id, timestamp, rank) VALUES (?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT credit_ranks_pk "
                + "DO UPDATE SET rank = EXCLUDED.rank WHERE credit_ranks.rank != EXCLUDED.rank";
        return update(connection, INSERT_ROW_SETTER, sql, creditRank);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM credit_ranks WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM credit_ranks");
    }
}
