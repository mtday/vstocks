package vstocks.db.jdbc.table;

import vstocks.model.*;

import java.sql.Connection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Collections.singleton;
import static vstocks.model.DatabaseField.USERNAME;

public class UserTable extends BaseTable {
    private static final RowMapper<User> ROW_MAPPER = rs ->
            new User()
                .setId(rs.getString("id"))
                .setUsername(rs.getString("username"))
                .setSource(UserSource.valueOf(rs.getString("source")))
                .setDisplayName(rs.getString("display_name"));

    private static final RowSetter<User> INSERT_ROW_SETTER = (ps, user) -> {
        int index = 0;
        ps.setString(++index, user.getId());
        ps.setString(++index, user.getUsername());
        ps.setString(++index, user.getSource().name());
        ps.setString(++index, user.getDisplayName());
    };

    private static final RowSetter<User> UPDATE_ROW_SETTER = (ps, user) -> {
        int index = 0;
        ps.setString(++index, user.getUsername());
        ps.setString(++index, user.getSource().name());
        ps.setString(++index, user.getDisplayName());
        ps.setString(++index, user.getId());
        ps.setString(++index, user.getUsername());
        ps.setString(++index, user.getSource().name());
        ps.setString(++index, user.getDisplayName());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return singleton(USERNAME.toSort());
    }

    public boolean usernameExists(Connection connection, String username) {
        return getCount(connection, "SELECT COUNT(*) FROM users WHERE username = ?", username) > 0;
    }

    public Optional<User> get(Connection connection, String id) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM users WHERE id = ?", id);
    }

    public int login(Connection connection, User user) {
        String sql = "INSERT INTO users (id, username, source, display_name) VALUES (?, ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT users_pk DO UPDATE SET username = EXCLUDED.username, "
                + "source = EXCLUDED.source, display_name = EXCLUDED.display_name "
                + "WHERE users.username != EXCLUDED.username OR users.source != EXCLUDED.source "
                + "OR users.display_name != EXCLUDED.display_name";
        return update(connection, sql, user.getId(), user.getUsername(), user.getSource().name(), user.getDisplayName());
    }

    public Results<User> getAll(Connection connection, Page page, Set<Sort> sort) {
        return results(connection, ROW_MAPPER, page,
                format("SELECT * FROM users %s LIMIT ? OFFSET ?", getSort(sort)),
                "SELECT COUNT(*) FROM users");
    }

    public int consume(Connection connection, Consumer<User> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM users %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, User user) {
        String sql = "INSERT INTO users (id, username, source, display_name) "
                + "VALUES (?, ?, ?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, user);
    }

    public int update(Connection connection, User user) {
        String sql = "UPDATE users SET username = ?, source = ?, display_name = ? "
                + "WHERE id = ? AND (username != ? OR source != ? OR display_name != ?)";
        return update(connection, UPDATE_ROW_SETTER, sql, user);
    }

    public int delete(Connection connection, String id) {
        return update(connection, "DELETE FROM users WHERE id = ?", id);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM users");
    }
}
