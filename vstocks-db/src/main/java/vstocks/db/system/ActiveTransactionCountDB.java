package vstocks.db.system;

import vstocks.db.BaseTable;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.system.ActiveTransactionCount;
import vstocks.model.system.ActiveTransactionCountCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singleton;
import static java.util.concurrent.TimeUnit.DAYS;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.SortDirection.DESC;

class ActiveTransactionCountDB extends BaseTable {
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
    protected Set<Sort> getDefaultSort() {
        return singleton(TIMESTAMP.toSort(DESC));
    }

    public ActiveTransactionCount generate(Connection connection) {
        Instant oneDayAgo = Instant.now().truncatedTo(SECONDS).minusSeconds(DAYS.toSeconds(1));
        String sql = "SELECT NOW() AS timestamp, COUNT(*) AS count FROM activity_logs "
                + "WHERE market IS NOT NULL AND timestamp >= ?";
        return getOne(connection, ROW_MAPPER, sql, oneDayAgo).orElse(null); // there will always be a result
    }

    public ActiveTransactionCountCollection getLatest(Connection connection) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        List<ActiveTransactionCount> activeTransactionCounts = new ArrayList<>();
        String sql = "SELECT * FROM active_transaction_counts WHERE timestamp >= ? ORDER BY timestamp DESC";
        consume(connection, ROW_MAPPER, activeTransactionCounts::add, sql, earliest);

        return new ActiveTransactionCountCollection()
                .setCounts(activeTransactionCounts)
                .setDeltas(Delta.getDeltas(activeTransactionCounts, ActiveTransactionCount::getTimestamp, ActiveTransactionCount::getCount));
    }

    public Results<ActiveTransactionCount> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM active_transaction_counts %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM active_transaction_counts";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, ActiveTransactionCount activeTransactionCount) {
        String sql = "INSERT INTO active_transaction_counts (timestamp, count) VALUES (?, ?) "
                + "ON CONFLICT ON CONSTRAINT transaction_counts_pk DO UPDATE SET count = EXCLUDED.count "
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
