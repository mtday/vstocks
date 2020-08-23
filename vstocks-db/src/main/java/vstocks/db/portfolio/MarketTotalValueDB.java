package vstocks.db.portfolio;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.db.UserDB;
import vstocks.model.*;
import vstocks.model.portfolio.MarketTotalValue;
import vstocks.model.portfolio.MarketTotalValueCollection;
import vstocks.model.portfolio.ValuedUser;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.SortDirection.DESC;

class MarketTotalValueDB extends BaseDB {
    private static final String BATCH_SEQUENCE = "market_total_values_batch_sequence";

    private static final RowMapper<MarketTotalValue> ROW_MAPPER = rs ->
            new MarketTotalValue()
                    .setBatch(rs.getLong("batch"))
                    .setUserId(rs.getString("user_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setValue(rs.getLong("value"));

    private static final RowMapper<ValuedUser> USER_ROW_MAPPER = rs ->
            new ValuedUser()
                    .setUser(UserDB.ROW_MAPPER.map(rs))
                    .setBatch(rs.getLong("batch"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                    .setValue(rs.getLong("value"));

    private static final RowSetter<MarketTotalValue> INSERT_ROW_SETTER = (ps, marketTotalValue) -> {
        int index = 0;
        ps.setLong(++index, marketTotalValue.getBatch());
        ps.setString(++index, marketTotalValue.getUserId());
        ps.setTimestamp(++index, Timestamp.from(marketTotalValue.getTimestamp()));
        ps.setLong(++index, marketTotalValue.getValue());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return asList(BATCH.toSort(DESC), VALUE.toSort(DESC), USER_ID.toSort());
    }

    public long setCurrentBatch(Connection connection, long batch) {
        return setSequenceValue(connection, BATCH_SEQUENCE, batch);
    }

    public int generate(Connection connection) {
        long batch = getNextSequenceValue(connection, BATCH_SEQUENCE);
        String sql = "INSERT INTO market_total_values (batch, user_id, timestamp, value)"
                + "(SELECT ? AS batch, user_id, NOW(), SUM(value) AS value FROM ("
                + "  (SELECT id AS user_id, NULL AS market, NULL AS symbol, 0 AS value FROM users)"
                + "  UNION"
                + "  (SELECT DISTINCT ON (us.user_id, us.market, us.symbol)"
                + "    us.user_id, us.market, us.symbol, us.shares * sp.price AS value"
                + "  FROM user_stocks us"
                + "  JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)"
                + "  ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC)"
                + ") AS priced_stocks GROUP BY user_id ORDER BY value DESC)";
        return update(connection, sql, batch);
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

    public Results<MarketTotalValue> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM market_total_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM market_total_values";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public Results<ValuedUser> getUsers(Connection connection, Page page) {
        long batch = getCurrentSequenceValue(connection, BATCH_SEQUENCE);
        String sql = "SELECT * FROM market_total_values v JOIN users u ON (u.id = v.user_id) WHERE batch = ? "
                + "ORDER BY v.value DESC, u.username LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) FROM market_total_values WHERE batch = ?";
        return results(connection, USER_ROW_MAPPER, page, sql, count, batch);
    }

    public int add(Connection connection, MarketTotalValue marketTotalValue) {
        return addAll(connection, singleton(marketTotalValue));
    }

    public int addAll(Connection connection, Collection<MarketTotalValue> marketTotalValues) {
        String sql = "INSERT INTO market_total_values (batch, user_id, timestamp, value) VALUES (?, ?, ?, ?) "
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
