package vstocks.db.system;

import vstocks.db.BaseDB;
import vstocks.db.RowMapper;
import vstocks.db.RowSetter;
import vstocks.model.*;
import vstocks.model.system.TotalUserCount;
import vstocks.model.system.TotalUserCountCollection;

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

class TotalUserCountDB extends BaseDB {
    private static final RowMapper<TotalUserCount> ROW_MAPPER = rs ->
            new TotalUserCount()
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setCount(rs.getLong("count"));

    private static final RowSetter<TotalUserCount> INSERT_ROW_SETTER = (ps, totalUserCount) -> {
        int index = 0;
        ps.setTimestamp(++index, Timestamp.from(totalUserCount.getTimestamp()));
        ps.setLong(++index, totalUserCount.getCount());
    };

    @Override
    protected List<Sort> getDefaultSort() {
        return singletonList(TIMESTAMP.toSort(DESC));
    }

    public int generate(Connection connection) {
        String sql = "INSERT INTO total_user_counts (timestamp, count) "
                + "(SELECT NOW() AS timestamp, COUNT(*) AS count FROM users)";
        return update(connection, sql);
    }

    public TotalUserCountCollection getLatest(Connection connection) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        List<TotalUserCount> totalUserCounts = new ArrayList<>();
        String sql = "SELECT * FROM total_user_counts WHERE timestamp >= ? ORDER BY timestamp DESC";
        consume(connection, ROW_MAPPER, totalUserCounts::add, sql, earliest);

        return new TotalUserCountCollection()
                .setCounts(totalUserCounts)
                .setDeltas(Delta.getDeltas(totalUserCounts, TotalUserCount::getTimestamp, TotalUserCount::getCount));
    }

    public Results<TotalUserCount> getAll(Connection connection, Page page, List<Sort> sort) {
        String sql = format("SELECT * FROM total_user_counts %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM total_user_counts";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, TotalUserCount totalUserCount) {
        String sql = "INSERT INTO total_user_counts (timestamp, count) VALUES (?, ?) "
                + "ON CONFLICT ON CONSTRAINT total_user_counts_pk DO UPDATE SET count = EXCLUDED.count "
                + "WHERE total_user_counts.count != EXCLUDED.count";
        return update(connection, INSERT_ROW_SETTER, sql, totalUserCount);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        String sql = "DELETE FROM total_user_counts WHERE timestamp < ?";
        return update(connection, sql, cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM total_user_counts");
    }
}
