package vstocks.db.portfolio;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.db.UserDB;
import vstocks.model.*;
import vstocks.model.portfolio.TotalValue;
import vstocks.model.portfolio.TotalValueCollection;
import vstocks.model.portfolio.ValuedUser;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.SortDirection.DESC;

class TotalValueDB extends BaseDB {
    private static final String BATCH_SEQUENCE = "total_values_batch_sequence";

    private static final RowMapper<TotalValue> ROW_MAPPER = rs ->
            new TotalValue()
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

    private static final RowSetter<TotalValue> INSERT_ROW_SETTER = (ps, totalValue) -> {
        int index = 0;
        ps.setLong(++index, totalValue.getBatch());
        ps.setString(++index, totalValue.getUserId());
        ps.setTimestamp(++index, Timestamp.from(totalValue.getTimestamp()));
        ps.setLong(++index, totalValue.getValue());
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
        String sql = "INSERT INTO total_values (batch, user_id, timestamp, value) "
                + "(SELECT ? AS batch, user_id, NOW() AS timestamp, SUM(credits) + SUM(stocks) AS value FROM ("
                + "  SELECT user_id, 0 AS credits, SUM(value) AS stocks FROM ("
                + "    SELECT DISTINCT ON (us.user_id, us.market, us.symbol)"
                + "      us.user_id, us.market, us.symbol, us.shares * sp.price AS value"
                + "    FROM user_stocks us"
                + "    JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)"
                + "    ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC"
                + "  ) AS priced_stocks GROUP BY user_id"
                + "  UNION"
                + "  SELECT user_id, credits, 0 AS stocks FROM user_credits"
                + ") AS portfolio_values GROUP BY user_id ORDER BY value DESC)";
        return update(connection, sql, batch);
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

    public Results<TotalValue> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM total_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM total_values";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public Results<ValuedUser> getUsers(Connection connection, Page page) {
        long batch = getCurrentSequenceValue(connection, BATCH_SEQUENCE);
        String sql = "SELECT * FROM total_values v JOIN users u ON (u.id = v.user_id) WHERE batch = ? "
                + "ORDER BY v.value DESC, u.username LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) FROM total_values WHERE batch = ?";
        return results(connection, USER_ROW_MAPPER, page, sql, count, batch);
    }

    public int add(Connection connection, TotalValue totalValue) {
        String sql = "INSERT INTO total_values (batch, user_id, timestamp, value) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT total_values_pk "
                + "DO UPDATE SET value = EXCLUDED.value WHERE total_values.value != EXCLUDED.value";
        return update(connection, INSERT_ROW_SETTER, sql, totalValue);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM total_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM total_values");
    }
}
