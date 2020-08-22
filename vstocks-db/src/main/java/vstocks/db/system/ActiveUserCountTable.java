package vstocks.db.system;

import vstocks.db.jdbc.table.BaseTable;
import vstocks.db.jdbc.table.RowMapper;
import vstocks.db.jdbc.table.RowSetter;
import vstocks.model.*;
import vstocks.model.system.ActiveUserCount;
import vstocks.model.system.ActiveUserCountCollection;

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

class ActiveUserCountTable extends BaseTable {
    private static final RowMapper<ActiveUserCount> ROW_MAPPER = rs ->
            new ActiveUserCount()
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setCount(rs.getLong("count"));

    private static final RowSetter<ActiveUserCount> INSERT_ROW_SETTER = (ps, activeUserCount) -> {
        int index = 0;
        ps.setTimestamp(++index, Timestamp.from(activeUserCount.getTimestamp()));
        ps.setLong(++index, activeUserCount.getCount());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return singleton(TIMESTAMP.toSort(DESC));
    }

    public ActiveUserCount generate(Connection connection) {
        Instant oneDayAgo = Instant.now().truncatedTo(SECONDS).minusSeconds(DAYS.toSeconds(1));
        String sql = "SELECT NOW() AS timestamp, COUNT(*) AS value FROM ("
                + "SELECT user_id FROM activity_logs WHERE timestamp >= ? GROUP BY user_id) AS data";
        return getOne(connection, ROW_MAPPER, sql, oneDayAgo).orElse(null); // there will always be a result
    }

    public ActiveUserCountCollection getLatest(Connection connection) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        List<ActiveUserCount> activeUserCounts = new ArrayList<>();
        String sql = "SELECT * FROM active_user_counts WHERE timestamp >= ? ORDER BY timestamp DESC";
        consume(connection, ROW_MAPPER, activeUserCounts::add, sql, earliest);

        return new ActiveUserCountCollection()
                .setCounts(activeUserCounts)
                .setDeltas(Delta.getDeltas(activeUserCounts, ActiveUserCount::getTimestamp, ActiveUserCount::getCount));
    }

    public Results<ActiveUserCount> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM active_user_counts %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM active_user_counts";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, ActiveUserCount activeUserCount) {
        String sql = "INSERT INTO active_user_counts (timestamp, count) VALUES (?, ?) "
                + "ON CONFLICT ON CONSTRAINT active_user_counts_pk DO UPDATE SET count = EXCLUDED.count "
                + "WHERE active_user_counts.count != EXCLUDED.count";
        return update(connection, INSERT_ROW_SETTER, sql, activeUserCount);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        String sql = "DELETE FROM active_user_counts WHERE timestamp < ?";
        return update(connection, sql, cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM active_user_counts");
    }
}
