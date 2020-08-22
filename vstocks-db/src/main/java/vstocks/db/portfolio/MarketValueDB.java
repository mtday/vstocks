package vstocks.db.portfolio;

import vstocks.db.BaseTable;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.portfolio.MarketValue;
import vstocks.model.portfolio.MarketValueCollection;

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

class MarketValueDB extends BaseTable {
    private static final RowMapper<MarketValue> ROW_MAPPER = rs ->
            new MarketValue()
                    .setUserId(rs.getString("user_id"))
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setValue(rs.getLong("value"));

    private static final RowSetter<MarketValue> INSERT_ROW_SETTER = (ps, marketValue) -> {
        int index = 0;
        ps.setString(++index, marketValue.getUserId());
        ps.setString(++index, marketValue.getMarket().name());
        ps.setTimestamp(++index, Timestamp.from(marketValue.getTimestamp()));
        ps.setLong(++index, marketValue.getValue());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort()));
    }

    public int generate(Connection connection, Market market, Consumer<MarketValue> consumer) {
        String sql = "SELECT user_id, market, NOW() AS timestamp, SUM(value) AS value FROM (" +
                "  SELECT DISTINCT ON (us.user_id, us.market, us.symbol)" +
                "    us.user_id, us.market, us.symbol, us.shares * sp.price AS value" +
                "  FROM user_stocks us" +
                "  JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)" +
                "  WHERE us.market = ?" +
                "  ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC" +
                ") AS priced_stocks GROUP BY user_id, market ORDER BY value DESC";
        return consume(connection, ROW_MAPPER, consumer, sql, market);
    }

    public MarketValueCollection getLatest(Connection connection, String userId, Market market) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        String sql = "SELECT * FROM market_values WHERE timestamp >= ? AND user_id = ? AND market = ? ORDER BY timestamp DESC";
        List<MarketValue> values = new ArrayList<>();
        consume(connection, ROW_MAPPER, values::add, sql, earliest, userId, market);

        return new MarketValueCollection()
                .setValues(values)
                .setDeltas(Delta.getDeltas(values, MarketValue::getTimestamp, MarketValue::getValue));
    }

    public Map<Market, MarketValueCollection> getLatest(Connection connection, String userId) {
        Map<Market, MarketValueCollection> results = new TreeMap<>();
        for (Market market : Market.values()) {
            results.put(market, getLatest(connection, userId, market));
        }
        return results;
    }

    public Results<MarketValue> getAll(Connection connection, Market market, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM market_values WHERE market = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM market_values WHERE market = ?";
        return results(connection, ROW_MAPPER, page, sql, count, market);
    }

    public int add(Connection connection, MarketValue marketValue) {
        return addAll(connection, singleton(marketValue));
    }

    public int addAll(Connection connection, Collection<MarketValue> marketValues) {
        String sql = "INSERT INTO market_values (user_id, market, timestamp, value) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT market_values_pk "
                + "DO UPDATE SET value = EXCLUDED.value WHERE market_values.value != EXCLUDED.value";
        return updateBatch(connection, INSERT_ROW_SETTER, sql, marketValues);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM market_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM market_values");
    }
}
