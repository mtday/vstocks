package vstocks.db.portfolio;

import vstocks.db.BaseTable;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.portfolio.TotalRank;
import vstocks.model.portfolio.TotalRankCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.DatabaseField.VALUE;
import static vstocks.model.SortDirection.DESC;

class TotalRankDB extends BaseTable {
    private static final RowMapper<TotalRank> ROW_MAPPER = rs ->
            new TotalRank()
                    .setUserId(rs.getString("user_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setRank(rs.getLong("rank"));

    private static final RowSetter<TotalRank> INSERT_ROW_SETTER = (ps, marketRank) -> {
        int index = 0;
        ps.setString(++index, marketRank.getUserId());
        ps.setTimestamp(++index, Timestamp.from(marketRank.getTimestamp()));
        ps.setLong(++index, marketRank.getRank());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(VALUE.toSort(DESC), USER_ID.toSort()));
    }

    public int generate(Connection connection, Consumer<TotalRank> consumer) {
        String sql = "SELECT user_id, timestamp, ROW_NUMBER() OVER (ORDER BY value DESC) FROM (" +
                "  SELECT user_id, NOW() AS timestamp, SUM(credits) + SUM(stocks) AS value FROM (" +
                "    SELECT user_id, 0 AS credits, SUM(value) AS stocks FROM (" +
                "      SELECT DISTINCT ON (us.user_id, us.market, us.symbol)" +
                "        us.user_id, us.market, us.symbol, us.shares * sp.price AS value" +
                "      FROM user_stocks us" +
                "      JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)" +
                "      ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC" +
                "    ) AS priced_stocks GROUP BY user_id" +
                "    UNION" +
                "    SELECT user_id, credits, 0 AS stocks FROM user_credits" +
                "  ) AS portfolio_values GROUP BY user_id ORDER BY value DESC" +
                ") AS ordered_values";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public TotalRankCollection getLatest(Connection connection, String userId) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        String sql = "SELECT * FROM total_ranks WHERE timestamp >= ? AND user_id = ? ORDER BY timestamp DESC";
        List<TotalRank> ranks = new ArrayList<>();
        consume(connection, ROW_MAPPER, ranks::add, sql, earliest, userId);

        return new TotalRankCollection()
                .setRanks(ranks)
                .setDeltas(Delta.getDeltas(ranks, TotalRank::getTimestamp, r -> -r.getRank()));
    }

    public Results<TotalRank> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM total_ranks %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM total_ranks";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, TotalRank totalRank) {
        return addAll(connection, singleton(totalRank));
    }

    public int addAll(Connection connection, Collection<TotalRank> totalRanks) {
        String sql = "INSERT INTO total_ranks (user_id, timestamp, rank) VALUES (?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT total_ranks_pk "
                + "DO UPDATE SET rank = EXCLUDED.rank WHERE total_ranks.rank != EXCLUDED.rank";
        return updateBatch(connection, INSERT_ROW_SETTER, sql, totalRanks);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM total_ranks WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM total_ranks");
    }
}
