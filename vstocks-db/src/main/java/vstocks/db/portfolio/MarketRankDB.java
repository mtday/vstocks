package vstocks.db.portfolio;

import vstocks.db.BaseTable;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.portfolio.MarketRank;
import vstocks.model.portfolio.MarketRankCollection;

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

class MarketRankDB extends BaseTable {
    private static final RowMapper<MarketRank> ROW_MAPPER = rs ->
            new MarketRank()
                    .setUserId(rs.getString("user_id"))
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setRank(rs.getLong("rank"));

    private static final RowSetter<MarketRank> INSERT_ROW_SETTER = (ps, marketRank) -> {
        int index = 0;
        ps.setString(++index, marketRank.getUserId());
        ps.setString(++index, marketRank.getMarket().name());
        ps.setTimestamp(++index, Timestamp.from(marketRank.getTimestamp()));
        ps.setLong(++index, marketRank.getRank());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort()));
    }

    public int generate(Connection connection, Market market, Consumer<MarketRank> consumer) {
        String sql = "SELECT user_id, market, timestamp, ROW_NUMBER() OVER (ORDER BY value DESC) AS rank FROM (" +
                "  SELECT user_id, market, NOW() AS timestamp, SUM(value) AS value FROM (" +
                "    SELECT DISTINCT ON (us.user_id, us.market, us.symbol)" +
                "      us.user_id, us.market, us.symbol, us.shares * sp.price AS value" +
                "    FROM user_stocks us" +
                "    JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)" +
                "    WHERE us.market = ?" +
                "    ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC" +
                "  ) AS priced_stocks GROUP BY user_id, market ORDER BY value DESC" +
                ") AS ordered_values";
        return consume(connection, ROW_MAPPER, consumer, sql, market);
    }

    public MarketRankCollection getLatest(Connection connection, String userId, Market market) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        String sql = "SELECT * FROM market_ranks WHERE timestamp >= ? AND user_id = ? AND market = ? ORDER BY timestamp DESC";
        List<MarketRank> ranks = new ArrayList<>();
        consume(connection, ROW_MAPPER, ranks::add, sql, earliest, userId, market);

        return new MarketRankCollection()
                .setRanks(ranks)
                .setDeltas(Delta.getDeltas(ranks, MarketRank::getTimestamp, r -> -r.getRank()));
    }

    public Map<Market, MarketRankCollection> getLatest(Connection connection, String userId) {
        Map<Market, MarketRankCollection> results = new TreeMap<>();
        for (Market market : Market.values()) {
            results.put(market, getLatest(connection, userId, market));
        }
        return results;
    }

    public Results<MarketRank> getAll(Connection connection, Market market, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM market_ranks WHERE market = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM market_ranks WHERE market = ?";
        return results(connection, ROW_MAPPER, page, sql, count, market);
    }

    public int add(Connection connection, MarketRank marketRank) {
        return addAll(connection, singleton(marketRank));
    }

    public int addAll(Connection connection, Collection<MarketRank> marketRanks) {
        String sql = "INSERT INTO market_ranks (user_id, market, timestamp, rank) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT market_ranks_pk "
                + "DO UPDATE SET rank = EXCLUDED.rank WHERE market_ranks.rank != EXCLUDED.rank";
        return updateBatch(connection, INSERT_ROW_SETTER, sql, marketRanks);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM market_ranks WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM market_ranks");
    }
}
