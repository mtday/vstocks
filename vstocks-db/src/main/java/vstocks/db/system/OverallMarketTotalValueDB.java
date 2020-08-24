package vstocks.db.system;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.DeltaInterval;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.OverallMarketTotalValue;
import vstocks.model.system.OverallMarketTotalValueCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.Delta.getDeltas;
import static vstocks.model.SortDirection.DESC;

class OverallMarketTotalValueDB extends BaseDB {
    private static final RowMapper<OverallMarketTotalValue> ROW_MAPPER = rs ->
            new OverallMarketTotalValue()
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setValue(rs.getLong("value"));

    private static final RowSetter<OverallMarketTotalValue> INSERT_ROW_SETTER = (ps, overallMarketTotalValue) -> {
        int index = 0;
        ps.setTimestamp(++index, Timestamp.from(overallMarketTotalValue.getTimestamp()));
        ps.setLong(++index, overallMarketTotalValue.getValue());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return singletonList(TIMESTAMP.toSort(DESC));
    }

    public int generate(Connection connection) {
        String sql = "INSERT INTO overall_market_total_values (timestamp, value)"
                + "(SELECT NOW() AS timestamp, SUM(value) AS value FROM ("
                + "  SELECT DISTINCT ON (us.user_id, us.market, us.symbol)"
                + "    us.market, us.symbol, us.shares * sp.price AS value"
                + "  FROM user_stocks us"
                + "  JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)"
                + "  ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC"
                + ") AS priced_stocks)";
        return update(connection, sql);
    }

    public OverallMarketTotalValueCollection getLatest(Connection connection) {
        Instant earliest = DeltaInterval.getLast().getEarliest();

        String sql = "SELECT * FROM overall_market_total_values WHERE timestamp >= ? ORDER BY timestamp DESC";
        List<OverallMarketTotalValue> values = new ArrayList<>();
        consume(connection, ROW_MAPPER, values::add, sql, earliest);

        return new OverallMarketTotalValueCollection()
                .setValues(values)
                .setDeltas(getDeltas(values, OverallMarketTotalValue::getTimestamp, OverallMarketTotalValue::getValue));
    }

    public Results<OverallMarketTotalValue> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM overall_market_total_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM overall_market_total_values";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, OverallMarketTotalValue overallMarketTotalValue) {
        String sql = "INSERT INTO overall_market_total_values (timestamp, value) VALUES (?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, overallMarketTotalValue);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM overall_market_total_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM overall_market_total_values");
    }
}
