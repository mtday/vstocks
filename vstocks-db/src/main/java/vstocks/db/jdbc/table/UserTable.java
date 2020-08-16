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
                    .setEmail(rs.getString("email"))
                    .setUsername(rs.getString("username"))
                    .setDisplayName(rs.getString("display_name"))
                    .setProfileImage(rs.getString("profile_image"));

    private static final RowSetter<User> INSERT_ROW_SETTER = (ps, user) -> {
        int index = 0;
        ps.setString(++index, user.getId());
        ps.setString(++index, user.getEmail());
        ps.setString(++index, user.getUsername());
        ps.setString(++index, user.getDisplayName());
        ps.setString(++index, user.getProfileImage());
    };

    private static final RowSetter<User> UPDATE_ROW_SETTER = (ps, user) -> {
        int index = 0;
        ps.setString(++index, user.getUsername());
        ps.setString(++index, user.getDisplayName());
        ps.setString(++index, user.getProfileImage());
        ps.setString(++index, user.getId());
        ps.setString(++index, user.getEmail());
        ps.setString(++index, user.getUsername());
        ps.setString(++index, user.getDisplayName());
        ps.setString(++index, user.getProfileImage());
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
        String sql = "INSERT INTO users (id, email, username, display_name, profile_image) VALUES (?, LOWER(?), ?, ?, ?) "
                + "ON CONFLICT ON CONSTRAINT users_pk DO UPDATE SET username = EXCLUDED.username, "
                + "display_name = EXCLUDED.display_name, profile_image = EXCLUDED.profile_image "
                + "WHERE users.username != EXCLUDED.username OR users.display_name != EXCLUDED.display_name "
                + "OR COALESCE(users.profile_image, '') != COALESCE(EXCLUDED.profile_image, '')";
        return update(connection, INSERT_ROW_SETTER, sql, user);
    }

    public int update(Connection connection, User user) {
        String sql = "UPDATE users SET username = ?, display_name = ?, profile_image = ? "
                + "WHERE id = ? AND email = LOWER(?) AND "
                + "(username != ? OR display_name != ? OR COALESCE(profile_image, '') != COALESCE(?, ''))";
        return update(connection, UPDATE_ROW_SETTER, sql, user);
    }

    public int delete(Connection connection, String id) {
        return update(connection, "DELETE FROM users WHERE id = ?", id);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM users");
    }
}
