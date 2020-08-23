package vstocks.db.portfolio;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.db.UserDB;
import vstocks.model.*;
import vstocks.model.portfolio.CreditValue;
import vstocks.model.portfolio.CreditValueCollection;
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

class CreditValueDB extends BaseDB {
    private static final String BATCH_SEQUENCE = "credit_values_batch_sequence";

    private static final RowMapper<CreditValue> ROW_MAPPER = rs ->
            new CreditValue()
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

    private static final RowSetter<CreditValue> INSERT_ROW_SETTER = (ps, creditValue) -> {
        int index = 0;
        ps.setLong(++index, creditValue.getBatch());
        ps.setString(++index, creditValue.getUserId());
        ps.setTimestamp(++index, Timestamp.from(creditValue.getTimestamp()));
        ps.setLong(++index, creditValue.getValue());
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
        String sql = "INSERT INTO credit_values (batch, user_id, timestamp, value) "
                + "(SELECT ? AS batch, user_id, NOW(), credits FROM user_credits)";
        return update(connection, sql, batch);
    }

    public CreditValueCollection getLatest(Connection connection, String userId) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        String sql = "SELECT * FROM credit_values WHERE timestamp >= ? AND user_id = ? ORDER BY timestamp DESC";
        List<CreditValue> values = new ArrayList<>();
        consume(connection, ROW_MAPPER, values::add, sql, earliest, userId);

        return new CreditValueCollection()
                .setValues(values)
                .setDeltas(Delta.getDeltas(values, CreditValue::getTimestamp, CreditValue::getValue));
    }

    public Results<CreditValue> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM credit_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM credit_values";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public Results<ValuedUser> getUsers(Connection connection, Page page) {
        long batch = getCurrentSequenceValue(connection, BATCH_SEQUENCE);
        String sql = "SELECT * FROM credit_values v JOIN users u ON (u.id = v.user_id) WHERE batch = ? "
                + "ORDER BY v.value DESC, u.username LIMIT ? OFFSET ?";
        String count = "SELECT COUNT(*) FROM credit_values WHERE batch = ?";
        return results(connection, USER_ROW_MAPPER, page, sql, count, batch);
    }

    public int add(Connection connection, CreditValue creditValue) {
        String sql = "INSERT INTO credit_values (batch, user_id, timestamp, value) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT credit_values_pk "
                + "DO UPDATE SET value = EXCLUDED.value WHERE credit_values.value != EXCLUDED.value";
        return update(connection, INSERT_ROW_SETTER, sql, creditValue);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM credit_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM credit_values");
    }
}
