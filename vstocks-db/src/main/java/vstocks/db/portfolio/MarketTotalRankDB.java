package vstocks.db.portfolio;

import vstocks.db.BaseTable;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.portfolio.MarketTotalRank;
import vstocks.model.portfolio.MarketTotalRankCollection;

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

class MarketTotalRankDB extends BaseTable {
    private static final RowMapper<MarketTotalRank> ROW_MAPPER = rs ->
            new MarketTotalRank()
                    .setUserId(rs.getString("user_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setRank(rs.getLong("rank"));

    private static final RowSetter<MarketTotalRank> INSERT_ROW_SETTER = (ps, marketTotalRank) -> {
        int index = 0;
        ps.setString(++index, marketTotalRank.getUserId());
        ps.setTimestamp(++index, Timestamp.from(marketTotalRank.getTimestamp()));
        ps.setLong(++index, marketTotalRank.getRank());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(RANK.toSort(), USER_ID.toSort()));
    }

    public int generate(Connection connection) {
        String sql = "INSERT INTO market_total_ranks (user_id, timestamp, rank)"
                + "(SELECT user_id, timestamp, RANK() OVER (ORDER BY value DESC) FROM ("
                + "  SELECT user_id, NOW() AS timestamp, SUM(value) AS value FROM ("
                + "    (SELECT id AS user_id, NULL AS market, NULL AS symbol, 0 AS value FROM users)"
                + "    UNION"
                + "    (SELECT DISTINCT ON (us.user_id, us.market, us.symbol)"
                + "      us.user_id, us.market, us.symbol, us.shares * sp.price AS value"
                + "    FROM user_stocks us"
                + "    JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)"
                + "    ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC)"
                + "  ) AS priced_stocks GROUP BY user_id ORDER BY value DESC"
                + ") AS ordered_values)";
        return update(connection, sql);
    }

    public MarketTotalRankCollection getLatest(Connection connection, String userId) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        String sql = "SELECT * FROM market_total_ranks WHERE timestamp >= ? AND user_id = ? ORDER BY timestamp DESC";
        List<MarketTotalRank> ranks = new ArrayList<>();
        consume(connection, ROW_MAPPER, ranks::add, sql, earliest, userId);

        return new MarketTotalRankCollection()
                .setRanks(ranks)
                .setDeltas(Delta.getDeltas(ranks, MarketTotalRank::getTimestamp, MarketTotalRank::getRank));
    }

    public Results<MarketTotalRank> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM market_total_ranks %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM market_total_ranks";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, MarketTotalRank marketTotalRank) {
        String sql = "INSERT INTO market_total_ranks (user_id, timestamp, rank) VALUES (?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT market_total_ranks_pk "
                + "DO UPDATE SET rank = EXCLUDED.rank WHERE market_total_ranks.rank != EXCLUDED.rank";
        return update(connection, INSERT_ROW_SETTER, sql, marketTotalRank);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM market_total_ranks WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM market_total_ranks");
    }
}
