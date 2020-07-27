package vstocks.service.jdbc.table;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.model.UserSource;

import java.sql.Connection;
import java.util.Optional;

public class UserTable extends BaseTable<User> {
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

    public boolean usernameExists(Connection connection, String username) {
        return getCount(connection, "SELECT COUNT(*) FROM users WHERE username = ?", username) > 0;
    }

    public Optional<User> get(Connection connection, String id) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM users WHERE id = ?", id);
    }

    public int login(Connection connection, User user) {
        // TODO: May need to revise this SQL when switching from H2 to another database
        String sql = "MERGE INTO users USING DUAL ON (id = ?) "
                + "WHEN NOT MATCHED THEN INSERT VALUES (?, ?, ?, ?) "
                + "WHEN MATCHED THEN UPDATE SET username = ?, source = ?, display_name = ? "
                + "WHERE id = ? AND (username != ? OR source != ? OR display_name != ?)";
        return update(connection, sql, user.getId(), user.getId(), user.getUsername(), user.getSource().name(),
                user.getDisplayName(), user.getUsername(), user.getSource().name(), user.getDisplayName(),
                user.getId(), user.getUsername(), user.getSource().name(), user.getDisplayName());
    }

    public Results<User> getAll(Connection connection, Page page) {
        return results(connection, ROW_MAPPER, page,
                "SELECT * FROM users LIMIT ? OFFSET ?",
                "SELECT COUNT(*) FROM users");
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
