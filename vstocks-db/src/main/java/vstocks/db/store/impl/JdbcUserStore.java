package vstocks.db.store.impl;

import vstocks.db.store.UserStore;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.model.UserSource;

import java.sql.Connection;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class JdbcUserStore extends BaseStore<User> implements UserStore {
    private static final RowMapper<User> ROW_MAPPER = rs -> {
        User user = new User()
                .setId(rs.getString("id"))
                .setUsername(rs.getString("username"))
                .setEmail(rs.getString("email"))
                .setSource(UserSource.valueOf(rs.getString("source")));
        ofNullable(rs.getString("hashed_pass")).ifPresent(user::setHashedPass);
        return user;
    };

    private static final RowSetter<User> INSERT_ROW_SETTER = (ps, user) -> {
        int index = 0;
        ps.setString(++index, user.getId());
        ps.setString(++index, user.getUsername());
        ps.setString(++index, user.getEmail());
        ps.setString(++index, user.getSource().name());
        ps.setString(++index, user.getHashedPass());
    };

    private static final RowSetter<User> UPDATE_ROW_SETTER = (ps, user) -> {
        int index = 0;
        ps.setString(++index, user.getUsername());
        ps.setString(++index, user.getEmail());
        ps.setString(++index, user.getId());
    };

    private static final RowSetter<User> UPDATE_PASSWORD_ROW_SETTER = (ps, user) -> {
        int index = 0;
        ps.setString(++index, user.getSource().name());
        ps.setString(++index, user.getHashedPass());
        ps.setString(++index, user.getId());
    };

    @Override
    public Optional<User> get(Connection connection, String id) {
        return getOne(connection, ROW_MAPPER, "SELECT * FROM users WHERE id = ?", id);
    }

    @Override
    public Optional<User> login(Connection connection, String login, String hashedPass) {
        String sql = "SELECT * FROM users WHERE (username = ? OR email = LOWER(?)) AND hashed_pass = ?";
        return getOne(connection, ROW_MAPPER, sql, login, login, hashedPass);
    }

    @Override
    public Results<User> getAll(Connection connection, Page page) {
        return results(connection, ROW_MAPPER, page,
                "SELECT * FROM users LIMIT ? OFFSET ?",
                "SELECT COUNT(*) FROM users");
    }

    @Override
    public int add(Connection connection, User user) {
        String sql = "INSERT INTO users (id, username, email, source, hashed_pass) "
                + "VALUES (?, ?, LOWER(?), ?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, user);
    }

    @Override
    public int update(Connection connection, User user) {
        String sql = "UPDATE users SET username = ?, email = LOWER(?) WHERE id = ?";
        return update(connection, UPDATE_ROW_SETTER, sql, user);
    }

    @Override
    public int updatePassword(Connection connection, User user) {
        String sql = "UPDATE users SET source = ?, hashed_pass = ? WHERE id = ?";
        return update(connection, UPDATE_PASSWORD_ROW_SETTER, sql, user);
    }

    @Override
    public int delete(Connection connection, String id) {
        return update(connection, "DELETE FROM users WHERE id = ?", id);
    }

    @Override
    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM users");
    }
}
