package vstocks.db.jdbc.table;

import vstocks.model.*;

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

public class UserCountTable extends BaseTable {
    private static final RowMapper<UserCount> ROW_MAPPER = rs ->
            new UserCount()
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant().truncatedTo(SECONDS))
                    .setUsers(rs.getLong("users"));

    private static final RowSetter<UserCount> INSERT_ROW_SETTER = (ps, userCount) -> {
        int index = 0;
        ps.setTimestamp(++index, Timestamp.from(userCount.getTimestamp()));
        ps.setLong(++index, userCount.getUsers());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return singleton(TIMESTAMP.toSort(DESC));
    }

    public UserCount generateTotal(Connection connection) {
        String sql = "SELECT NOW() AS timestamp, COUNT(id) AS users FROM users";
        return getOne(connection, ROW_MAPPER, sql).orElse(null); // there will always be a result
    }

    public UserCount generateActive(Connection connection) {
        Instant oneDayAgo = Instant.now().truncatedTo(SECONDS).minusSeconds(DAYS.toSeconds(1));
        String sql = "SELECT NOW() AS timestamp, COUNT(*) AS users FROM ("
                + "SELECT user_id FROM activity_logs WHERE timestamp >= ? GROUP BY user_id) AS data";
        return getOne(connection, ROW_MAPPER, sql, oneDayAgo).orElse(null); // there will always be a result
    }

    private UserCount getTotal(Connection connection, String table) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        List<UserCount> values = new ArrayList<>();
        String sql = format("SELECT * FROM %s WHERE timestamp >= ? ORDER BY timestamp DESC", table);
        consume(connection, ROW_MAPPER, values::add, sql, earliest);

        UserCount userCount = values.stream().findFirst().orElseGet(() ->
                new UserCount().setTimestamp(Instant.now().truncatedTo(SECONDS)).setUsers(0));
        userCount.setDeltas(Delta.getDeltas(values, UserCount::getTimestamp, UserCount::getUsers));
        return userCount;
    }

    public UserCount getLatestTotal(Connection connection) {
        return getTotal(connection, "total_user_counts");
    }

    public UserCount getLatestActive(Connection connection) {
        return getTotal(connection, "active_user_counts");
    }

    private Results<UserCount> getAll(Connection connection, String table, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM %s %s LIMIT ? OFFSET ?", table, getSort(sort));
        String count = format("SELECT COUNT(*) FROM %s", table);
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public Results<UserCount> getAllTotal(Connection connection, Page page, Set<Sort> sort) {
        return getAll(connection, "total_user_counts", page, sort);
    }

    public Results<UserCount> getAllActive(Connection connection, Page page, Set<Sort> sort) {
        return getAll(connection, "active_user_counts", page, sort);
    }

    private int add(Connection connection, String table, UserCount userCount) {
        String sql = format("INSERT INTO %s (timestamp, users) VALUES (?, ?) "
                + "ON CONFLICT ON CONSTRAINT %s_pk DO UPDATE SET users = EXCLUDED.users "
                + "WHERE %s.users != EXCLUDED.users", table, table, table);
        return update(connection, INSERT_ROW_SETTER, sql, userCount);
    }

    public int addTotal(Connection connection, UserCount userCount) {
        return add(connection, "total_user_counts", userCount);
    }

    public int addActive(Connection connection, UserCount userCount) {
        return add(connection, "active_user_counts", userCount);
    }

    private int ageOff(Connection connection, String table, Instant cutoff) {
        String sql = format("DELETE FROM %s WHERE timestamp < ?", table);
        return update(connection, sql, cutoff);
    }

    public int ageOffTotal(Connection connection, Instant cutoff) {
        return ageOff(connection, "total_user_counts", cutoff);
    }

    public int ageOffActive(Connection connection, Instant cutoff) {
        return ageOff(connection, "active_user_counts", cutoff);
    }

    public int truncateTotal(Connection connection) {
        return update(connection, "DELETE FROM total_user_counts");
    }

    public int truncateActive(Connection connection) {
        return update(connection, "DELETE FROM active_user_counts");
    }
}
