package vstocks.db.system;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.system.OverallMarketValue;
import vstocks.model.system.OverallMarketValueCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.Delta.getDeltas;
import static vstocks.model.SortDirection.DESC;

class OverallMarketValueDB extends BaseDB {
    private static final RowMapper<OverallMarketValue> ROW_MAPPER = rs ->
            new OverallMarketValue()
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setValue(rs.getLong("value"));

    private static final RowSetter<OverallMarketValue> INSERT_ROW_SETTER = (ps, overallMarketValue) -> {
        int index = 0;
        ps.setString(++index, overallMarketValue.getMarket().name());
        ps.setTimestamp(++index, Timestamp.from(overallMarketValue.getTimestamp()));
        ps.setLong(++index, overallMarketValue.getValue());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return singletonList(TIMESTAMP.toSort(DESC));
    }

    private int generate(Connection connection, Market market) {
        String sql = "INSERT INTO overall_market_values (market, timestamp, value)"
                + "(SELECT market, NOW() AS timestamp, SUM(value) AS value FROM ("
                + "  (SELECT ? AS market, NULL AS symbol, 0 AS value)"
                + "  UNION"
                + "  (SELECT DISTINCT ON (us.user_id, us.market, us.symbol)"
                + "    us.market, us.symbol, us.shares * sp.price AS value"
                + "  FROM user_stocks us"
                + "  JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)"
                + "  WHERE us.market = ?"
                + "  ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC)"
                + ") AS priced_stocks GROUP BY market)";
        return update(connection, sql, market, market);
    }

    public int generate(Connection connection) {
        return Arrays.stream(Market.values()).mapToInt(market -> generate(connection, market)).sum();
    }

    public OverallMarketValueCollection getLatest(Connection connection, Market market) {
        Instant earliest = DeltaInterval.getLast().getEarliest();

        String sql = "SELECT * FROM overall_market_values WHERE timestamp >= ? AND market = ? ORDER BY timestamp DESC";
        List<OverallMarketValue> values = new ArrayList<>();
        consume(connection, ROW_MAPPER, values::add, sql, earliest, market);

        return new OverallMarketValueCollection()
                .setMarket(market)
                .setValues(values)
                .setDeltas(getDeltas(values, OverallMarketValue::getTimestamp, OverallMarketValue::getValue));
    }

    public Map<Market, OverallMarketValueCollection> getLatest(Connection connection) {
        Map<Market, OverallMarketValueCollection> values = new TreeMap<>();
        for (Market market : Market.values()) {
            values.put(market, getLatest(connection, market));
        }
        return values;
    }

    public Results<OverallMarketValue> getAll(Connection connection, Market market, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM overall_market_values WHERE market = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM overall_market_values WHERE market = ?";
        return results(connection, ROW_MAPPER, page, sql, count, market);
    }

    public int add(Connection connection, OverallMarketValue overallMarketValue) {
        String sql = "INSERT INTO overall_market_values (market, timestamp, value) VALUES (?, ?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, overallMarketValue);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM overall_market_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM overall_market_values");
    }
}
