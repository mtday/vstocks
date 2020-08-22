package vstocks.db.system;

import vstocks.db.BaseTable;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.system.TotalTransactionCount;
import vstocks.model.system.TotalTransactionCountCollection;

import java.sql.Connection;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singleton;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.SortDirection.DESC;

class TotalTransactionCountTable extends BaseTable {
    private static final RowMapper<TotalTransactionCount> ROW_MAPPER = rs ->
            new TotalTransactionCount()
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setCount(rs.getLong("count"));

    private static final RowSetter<TotalTransactionCount> INSERT_ROW_SETTER = (ps, totalTransactionCount) -> {
        int index = 0;
        ps.setTimestamp(++index, Timestamp.from(totalTransactionCount.getTimestamp()));
        ps.setLong(++index, totalTransactionCount.getCount());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return singleton(TIMESTAMP.toSort(DESC));
    }

    public TotalTransactionCount generate(Connection connection) {
        String sql = "SELECT NOW() AS timestamp, COUNT(*) AS count FROM activity_logs WHERE market IS NOT NULL";
        return getOne(connection, ROW_MAPPER, sql).orElse(null); // there will always be a result
    }

    public TotalTransactionCountCollection getLatest(Connection connection) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        List<TotalTransactionCount> totalTransactionCounts = new ArrayList<>();
        String sql = "SELECT * FROM total_transaction_counts WHERE timestamp >= ? ORDER BY timestamp DESC";
        consume(connection, ROW_MAPPER, totalTransactionCounts::add, sql, earliest);

        return new TotalTransactionCountCollection()
                .setCounts(totalTransactionCounts)
                .setDeltas(Delta.getDeltas(totalTransactionCounts, TotalTransactionCount::getTimestamp, TotalTransactionCount::getCount));
    }

    public Results<TotalTransactionCount> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM total_transaction_counts %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM total_transaction_counts";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, TotalTransactionCount totalTransactionCount) {
        String sql = "INSERT INTO total_transaction_counts (timestamp, count) VALUES (?, ?) "
                + "ON CONFLICT ON CONSTRAINT transaction_counts_pk DO UPDATE SET count = EXCLUDED.count "
                + "WHERE total_transaction_counts.count != EXCLUDED.count";
        return update(connection, INSERT_ROW_SETTER, sql, totalTransactionCount);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        String sql = "DELETE FROM total_transaction_counts WHERE timestamp < ?";
        return update(connection, sql, cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM total_transaction_counts");
    }
}
