package vstocks.db.portfolio;

import vstocks.db.jdbc.table.BaseTable;
import vstocks.db.jdbc.table.RowMapper;
import vstocks.db.jdbc.table.RowSetter;
import vstocks.model.*;
import vstocks.model.portfolio.MarketTotalValue;
import vstocks.model.portfolio.MarketTotalValueCollection;

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

class MarketTotalValueTable extends BaseTable {
    private static final RowMapper<MarketTotalValue> ROW_MAPPER = rs ->
            new MarketTotalValue()
                    .setUserId(rs.getString("user_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setValue(rs.getLong("value"));

    private static final RowSetter<MarketTotalValue> INSERT_ROW_SETTER = (ps, marketTotalValue) -> {
        int index = 0;
        ps.setString(++index, marketTotalValue.getUserId());
        ps.setTimestamp(++index, Timestamp.from(marketTotalValue.getTimestamp()));
        ps.setLong(++index, marketTotalValue.getValue());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort()));
    }

    public int generate(Connection connection, Consumer<MarketTotalValue> consumer) {
        String sql = "SELECT user_id, NOW() AS timestamp, SUM(value) AS value FROM (" +
                "  SELECT DISTINCT ON (us.user_id, us.market, us.symbol)" +
                "    us.user_id, us.market, us.symbol, us.shares * sp.price AS value" +
                "  FROM user_stocks us" +
                "  JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)" +
                "  ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC" +
                ") AS priced_stocks GROUP BY user_id ORDER BY value DESC";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public MarketTotalValueCollection getLatest(Connection connection, String userId) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        String sql = "SELECT * FROM market_total_values WHERE timestamp >= ? AND user_id = ? ORDER BY timestamp DESC";
        List<MarketTotalValue> values = new ArrayList<>();
        consume(connection, ROW_MAPPER, values::add, sql, earliest, userId);

        return new MarketTotalValueCollection()
                .setValues(values)
                .setDeltas(Delta.getDeltas(values, MarketTotalValue::getTimestamp, MarketTotalValue::getValue));
    }

    public Results<MarketTotalValue> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM market_total_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM market_total_values";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, MarketTotalValue marketTotalValue) {
        return addAll(connection, singleton(marketTotalValue));
    }

    public int addAll(Connection connection, Collection<MarketTotalValue> marketTotalValues) {
        String sql = "INSERT INTO market_total_values (user_id, timestamp, value) VALUES (?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT market_total_values_pk "
                + "DO UPDATE SET value = EXCLUDED.value WHERE market_total_values.value != EXCLUDED.value";
        return updateBatch(connection, INSERT_ROW_SETTER, sql, marketTotalValues);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM market_total_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM market_total_values");
    }
}
