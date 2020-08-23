package vstocks.db.portfolio;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.db.UserDB;
import vstocks.model.*;
import vstocks.model.portfolio.MarketValue;
import vstocks.model.portfolio.MarketValueCollection;
import vstocks.model.portfolio.ValuedUser;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.SortDirection.DESC;

class MarketValueDB extends BaseDB {
    private static final String BATCH_SEQUENCE = "market_values_batch_sequence";

    private static final RowMapper<MarketValue> ROW_MAPPER = rs ->
            new MarketValue()
                    .setBatch(rs.getLong("batch"))
                    .setUserId(rs.getString("user_id"))
                    .setMarket(Market.valueOf(rs.getString("market")))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setValue(rs.getLong("value"));

    private static final RowMapper<ValuedUser> USER_ROW_MAPPER = rs ->
            new ValuedUser()
                    .setUser(UserDB.ROW_MAPPER.map(rs))
                    .setBatch(rs.getLong("batch"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                    .setValue(rs.getLong("value"));

    private static final RowSetter<MarketValue> INSERT_ROW_SETTER = (ps, marketValue) -> {
        int index = 0;
        ps.setLong(++index, marketValue.getBatch());
        ps.setString(++index, marketValue.getUserId());
        ps.setString(++index, marketValue.getMarket().name());
        ps.setTimestamp(++index, Timestamp.from(marketValue.getTimestamp()));
        ps.setLong(++index, marketValue.getValue());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return asList(BATCH.toSort(DESC), VALUE.toSort(DESC), USER_ID.toSort());
    }

    public long setCurrentBatch(Connection connection, long batch) {
        return setSequenceValue(connection, BATCH_SEQUENCE, batch);
    }

    private int generate(Connection connection, long batch, Market market) {
        String sql = "INSERT INTO market_values (batch, user_id, market, timestamp, value)"
                + "(SELECT ? AS batch, user_id, market, NOW() AS timestamp, SUM(value) AS value FROM ("
                + "  (SELECT id AS user_id, ? AS market, NULL AS symbol, 0 AS value FROM users)"
                + "  UNION"
                + "  (SELECT DISTINCT ON (us.user_id, us.market, us.symbol)"
                + "    us.user_id, us.market, us.symbol, us.shares * sp.price AS value"
                + "  FROM user_stocks us"
                + "  JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)"
                + "  WHERE us.market = ?"
                + "  ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC)"
                + ") AS priced_stocks GROUP BY user_id, market ORDER BY value DESC)";
        return update(connection, sql, batch, market, market);
    }

    public int generate(Connection connection) {
        long batch = getNextSequenceValue(connection, BATCH_SEQUENCE);
        int sum = 0;
        for (Market market : Market.values()) {
            sum += generate(connection, batch, market);
        }
        return sum;
    }

    public MarketValueCollection getLatest(Connection connection, String userId, Market market) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        String sql = "SELECT * FROM market_values WHERE timestamp >= ? AND user_id = ? AND market = ? "
                + "ORDER BY timestamp DESC";
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

    public Results<MarketValue> getAll(Connection connection, Market market, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM market_values WHERE market = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM market_values WHERE market = ?";
        return results(connection, ROW_MAPPER, page, sql, count, market);
    }

    public Results<ValuedUser> getUsers(Connection connection, Market market, Page page) {
        long batch = getCurrentSequenceValue(connection, BATCH_SEQUENCE);
        String sql = "SELECT * FROM market_values v JOIN users u ON (u.id = v.user_id) WHERE batch = ? AND market = ? "
                + "ORDER BY v.value DESC, u.username LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) FROM market_values WHERE batch = ? AND market = ?";
        return results(connection, USER_ROW_MAPPER, page, sql, count, batch, market);
    }

    public int add(Connection connection, MarketValue marketValue) {
        return addAll(connection, singleton(marketValue));
    }

    public int addAll(Connection connection, Collection<MarketValue> marketValues) {
        String sql = "INSERT INTO market_values (batch, user_id, market, timestamp, value) VALUES (?, ?, ?, ?, ?) "
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
