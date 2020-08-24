package vstocks.db.system;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.system.OverallCreditValue;
import vstocks.model.system.OverallCreditValueCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.SortDirection.DESC;

class OverallCreditValueDB extends BaseDB {
    private static final RowMapper<OverallCreditValue> ROW_MAPPER = rs ->
            new OverallCreditValue()
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setValue(rs.getLong("value"));

    private static final RowSetter<OverallCreditValue> INSERT_ROW_SETTER = (ps, overallCreditValue) -> {
        int index = 0;
        ps.setTimestamp(++index, Timestamp.from(overallCreditValue.getTimestamp()));
        ps.setLong(++index, overallCreditValue.getValue());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return singletonList(TIMESTAMP.toSort(DESC));
    }

    public int generate(Connection connection) {
        String sql = "INSERT INTO overall_credit_values (timestamp, value) "
                + "(SELECT NOW(), SUM(credits) FROM user_credits)";
        return update(connection, sql);
    }

    public OverallCreditValueCollection getLatest(Connection connection) {
        Instant earliest = DeltaInterval.getLast().getEarliest();

        String sql = "SELECT * FROM overall_credit_values WHERE timestamp >= ? ORDER BY timestamp DESC";
        List<OverallCreditValue> values = new ArrayList<>();
        consume(connection, ROW_MAPPER, values::add, sql, earliest);

        return new OverallCreditValueCollection()
                .setValues(values)
                .setDeltas(Delta.getDeltas(values, OverallCreditValue::getTimestamp, OverallCreditValue::getValue));
    }

    public Results<OverallCreditValue> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM overall_credit_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM overall_credit_values";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, OverallCreditValue overallCreditValue) {
        String sql = "INSERT INTO overall_credit_values (timestamp, value) VALUES (?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, overallCreditValue);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM overall_credit_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM overall_credit_values");
    }
}
