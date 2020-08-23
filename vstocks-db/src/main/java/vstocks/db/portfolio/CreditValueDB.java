package vstocks.db.portfolio;

import vstocks.db.BaseTable;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.portfolio.CreditValue;
import vstocks.model.portfolio.CreditValueCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.DatabaseField.VALUE;
import static vstocks.model.SortDirection.DESC;

class CreditValueDB extends BaseTable {
    private static final RowMapper<CreditValue> ROW_MAPPER = rs ->
            new CreditValue()
                    .setUserId(rs.getString("user_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setValue(rs.getLong("value"));

    private static final RowSetter<CreditValue> INSERT_ROW_SETTER = (ps, creditValue) -> {
        int index = 0;
        ps.setString(++index, creditValue.getUserId());
        ps.setTimestamp(++index, Timestamp.from(creditValue.getTimestamp()));
        ps.setLong(++index, creditValue.getValue());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new LinkedHashSet<>(asList(VALUE.toSort(DESC), USER_ID.toSort()));
    }

    public int generate(Connection connection) {
        String sql = "INSERT INTO credit_values (user_id, timestamp, value) "
                + "(SELECT user_id, NOW(), credits FROM user_credits)";
        return update(connection, sql);
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

    public Results<CreditValue> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM credit_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM credit_values";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, CreditValue creditValue) {
        return addAll(connection, singleton(creditValue));
    }

    public int addAll(Connection connection, Collection<CreditValue> creditValues) {
        String sql = "INSERT INTO credit_values (user_id, timestamp, value) VALUES (?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT credit_values_pk "
                + "DO UPDATE SET value = EXCLUDED.value WHERE credit_values.value != EXCLUDED.value";
        return updateBatch(connection, INSERT_ROW_SETTER, sql, creditValues);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM credit_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM credit_values");
    }
}
