package vstocks.db.portfolio;

import vstocks.db.BaseTable;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.portfolio.TotalValue;
import vstocks.model.portfolio.TotalValueCollection;

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

class TotalValueDB extends BaseTable {
    private static final RowMapper<TotalValue> ROW_MAPPER = rs ->
            new TotalValue()
                    .setUserId(rs.getString("user_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setValue(rs.getLong("value"));

    private static final RowSetter<TotalValue> INSERT_ROW_SETTER = (ps, marketValue) -> {
        int index = 0;
        ps.setString(++index, marketValue.getUserId());
        ps.setTimestamp(++index, Timestamp.from(marketValue.getTimestamp()));
        ps.setLong(++index, marketValue.getValue());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(VALUE.toSort(DESC), USER_ID.toSort()));
    }

    public int generate(Connection connection, Consumer<TotalValue> consumer) {
        String sql = "SELECT user_id, NOW() AS timestamp, SUM(credits) + SUM(stocks) AS value FROM (" +
                "  SELECT user_id, 0 AS credits, SUM(value) AS stocks FROM (" +
                "    SELECT DISTINCT ON (us.user_id, us.market, us.symbol)" +
                "      us.user_id, us.market, us.symbol, us.shares * sp.price AS value" +
                "    FROM user_stocks us" +
                "    JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)" +
                "    ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC" +
                "  ) AS priced_stocks GROUP BY user_id" +
                "  UNION" +
                "  SELECT user_id, credits, 0 AS stocks FROM user_credits" +
                ") AS portfolio_values GROUP BY user_id ORDER BY value DESC";
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public TotalValueCollection getLatest(Connection connection, String userId) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        String sql = "SELECT * FROM total_values WHERE timestamp >= ? AND user_id = ? ORDER BY timestamp DESC";
        List<TotalValue> values = new ArrayList<>();
        consume(connection, ROW_MAPPER, values::add, sql, earliest, userId);

        return new TotalValueCollection()
                .setValues(values)
                .setDeltas(Delta.getDeltas(values, TotalValue::getTimestamp, TotalValue::getValue));
    }

    public Results<TotalValue> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM total_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM total_values";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, TotalValue totalValue) {
        return addAll(connection, singleton(totalValue));
    }

    public int addAll(Connection connection, Collection<TotalValue> totalValues) {
        String sql = "INSERT INTO total_values (user_id, timestamp, value) VALUES (?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT total_values_pk "
                + "DO UPDATE SET value = EXCLUDED.value WHERE total_values.value != EXCLUDED.value";
        return updateBatch(connection, INSERT_ROW_SETTER, sql, totalValues);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM total_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM total_values");
    }
}
