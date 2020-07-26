package vstocks.service.jdbc;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.service.UserService;
import vstocks.service.jdbc.table.UserTable;

import javax.sql.DataSource;
import java.util.Optional;

public class JdbcUserService extends BaseService implements UserService {
    private final UserTable userTable = new UserTable();

    public JdbcUserService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<User> get(String id) {
        return withConnection(conn -> userTable.get(conn, id));
    }

    @Override
    public Optional<User> login(String login, String hashedPass) {
        return withConnection(conn -> userTable.login(conn, login, hashedPass));
    }

    @Override
    public Results<User> getAll(Page page) {
        return withConnection(conn -> userTable.getAll(conn, page));
    }

    @Override
    public int add(User user) {
        return withConnection(conn -> userTable.add(conn, user));
    }

    @Override
    public int update(User user) {
        return withConnection(conn -> userTable.update(conn, user));
    }

    @Override
    public int updatePassword(User user) {
        return withConnection(conn -> userTable.updatePassword(conn, user));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> userTable.delete(conn, id));
    }
}
