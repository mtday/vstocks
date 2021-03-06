package vstocks.db.system;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.DeltaInterval;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.ActiveTransactionCount;
import vstocks.model.system.ActiveTransactionCountCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.DAYS;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.Delta.getDeltas;
import static vstocks.model.SortDirection.DESC;

class ActiveTransactionCountDB extends BaseDB {
    private static final RowMapper<ActiveTransactionCount> ROW_MAPPER = rs ->
            new ActiveTransactionCount()
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setCount(rs.getLong("count"));

    private static final RowSetter<ActiveTransactionCount> INSERT_ROW_SETTER = (ps, activeTransactionCount) -> {
        int index = 0;
        ps.setTimestamp(++index, Timestamp.from(activeTransactionCount.getTimestamp()));
        ps.setLong(++index, activeTransactionCount.getCount());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return singletonList(TIMESTAMP.toSort(DESC));
    }

    public int generate(Connection connection) {
        Instant oneDayAgo = Instant.now().truncatedTo(SECONDS).minusSeconds(DAYS.toSeconds(1));
        String sql = "INSERT INTO active_transaction_counts (timestamp, count) "
                + "(SELECT NOW() AS timestamp, COUNT(*) AS count FROM activity_logs "
                + "WHERE market IS NOT NULL AND timestamp >= ?)";
        return update(connection, sql, oneDayAgo);
    }

    public ActiveTransactionCountCollection getLatest(Connection connection) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        List<ActiveTransactionCount> counts = new ArrayList<>();
        String sql = "SELECT * FROM active_transaction_counts WHERE timestamp >= ? ORDER BY timestamp DESC";
        consume(connection, ROW_MAPPER, counts::add, sql, earliest);

        return new ActiveTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(getDeltas(counts, ActiveTransactionCount::getTimestamp, ActiveTransactionCount::getCount));
    }

    public Results<ActiveTransactionCount> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM active_transaction_counts %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM active_transaction_counts";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, ActiveTransactionCount activeTransactionCount) {
        String sql = "INSERT INTO active_transaction_counts (timestamp, count) VALUES (?, ?) "
                + "ON CONFLICT ON CONSTRAINT active_transaction_counts_pk DO UPDATE SET count = EXCLUDED.count "
                + "WHERE active_transaction_counts.count != EXCLUDED.count";
        return update(connection, INSERT_ROW_SETTER, sql, activeTransactionCount);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        String sql = "DELETE FROM active_transaction_counts WHERE timestamp < ?";
        return update(connection, sql, cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM active_transaction_counts");
    }
}
