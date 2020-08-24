package vstocks.db.system;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.system.OverallTotalValue;
import vstocks.model.system.OverallTotalValueCollection;

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

class OverallTotalValueDB extends BaseDB {
    private static final RowMapper<OverallTotalValue> ROW_MAPPER = rs ->
            new OverallTotalValue()
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setValue(rs.getLong("value"));

    private static final RowSetter<OverallTotalValue> INSERT_ROW_SETTER = (ps, overallTotalValue) -> {
        int index = 0;
        ps.setTimestamp(++index, Timestamp.from(overallTotalValue.getTimestamp()));
        ps.setLong(++index, overallTotalValue.getValue());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return singletonList(TIMESTAMP.toSort(DESC));
    }

    public int generate(Connection connection) {
        String sql = "INSERT INTO overall_total_values (timestamp, value)"
                + "(SELECT NOW() AS timestamp, SUM(credits) + SUM(stocks) AS value FROM ("
                + "  SELECT 0 AS credits, SUM(value) AS stocks FROM ("
                + "    SELECT DISTINCT ON (us.user_id, us.market, us.symbol)"
                + "      us.market, us.symbol, us.shares * sp.price AS value"
                + "    FROM user_stocks us"
                + "    JOIN stock_prices sp ON (us.market = sp.market AND us.symbol = sp.symbol)"
                + "    ORDER BY us.user_id, us.market, us.symbol, sp.timestamp DESC"
                + "  ) AS priced_stocks"
                + "  UNION"
                + "  SELECT SUM(credits), 0 AS stocks FROM user_credits"
                + ") AS portfolio_values)";
        return update(connection, sql);
    }

    public OverallTotalValueCollection getLatest(Connection connection) {
        Instant earliest = DeltaInterval.getLast().getEarliest();

        String sql = "SELECT * FROM overall_total_values WHERE timestamp >= ? ORDER BY timestamp DESC";
        List<OverallTotalValue> values = new ArrayList<>();
        consume(connection, ROW_MAPPER, values::add, sql, earliest);

        return new OverallTotalValueCollection()
                .setValues(values)
                .setDeltas(Delta.getDeltas(values, OverallTotalValue::getTimestamp, OverallTotalValue::getValue));
    }

    public Results<OverallTotalValue> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM overall_total_values %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM overall_total_values";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, OverallTotalValue overallTotalValue) {
        String sql = "INSERT INTO overall_total_values (timestamp, value) VALUES (?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, overallTotalValue);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM overall_total_values WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM overall_total_values");
    }
}
