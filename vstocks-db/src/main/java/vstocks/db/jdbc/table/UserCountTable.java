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

    public UserCount generate(Connection connection) {
        String sql = "SELECT NOW() AS timestamp, COUNT(id) AS users FROM users";
        return getOne(connection, ROW_MAPPER, sql).orElse(null); // there will always be a result
    }

    public UserCount getLatest(Connection connection) {
        Instant earliest = DeltaInterval.values()[DeltaInterval.values().length - 1].getEarliest();

        List<UserCount> values = new ArrayList<>();
        String sql = "SELECT * FROM user_counts WHERE timestamp >= ? ORDER BY timestamp DESC";
        consume(connection, ROW_MAPPER, values::add, sql, earliest);

        UserCount userCount = values.stream().findFirst().orElseGet(() ->
                new UserCount().setTimestamp(Instant.now().truncatedTo(SECONDS)).setUsers(0));
        userCount.setDeltas(Delta.getDeltas(values, UserCount::getTimestamp, UserCount::getUsers));
        return userCount;
    }

    public Results<UserCount> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM user_counts %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM user_counts";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int add(Connection connection, UserCount userCount) {
        String sql = "INSERT INTO user_counts (timestamp, users) VALUES (?, ?) "
                + "ON CONFLICT ON CONSTRAINT user_counts_pk DO UPDATE SET users = EXCLUDED.users "
                + "WHERE user_counts.users != EXCLUDED.users";
        return update(connection, INSERT_ROW_SETTER, sql, userCount);
    }

    public int ageOff(Connection connection, Instant cutoff) {
        return update(connection, "DELETE FROM user_counts WHERE timestamp < ?", cutoff);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM user_counts");
    }
}
