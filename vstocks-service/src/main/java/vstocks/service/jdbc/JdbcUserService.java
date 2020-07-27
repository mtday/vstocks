package vstocks.service.jdbc;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.model.UserBalance;
import vstocks.service.UserService;
import vstocks.service.jdbc.table.UserBalanceTable;
import vstocks.service.jdbc.table.UserTable;

import javax.sql.DataSource;
import java.util.Optional;

import static vstocks.config.Config.USER_INITIAL_BALANCE;

public class JdbcUserService extends BaseService implements UserService {
    private final UserTable userTable = new UserTable();
    private final UserBalanceTable userBalanceTable = new UserBalanceTable();

    public JdbcUserService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean usernameExists(String username) {
        return withConnection(conn -> userTable.usernameExists(conn, username));
    }

    @Override
    public Optional<User> get(String id) {
        return withConnection(conn -> userTable.get(conn, id));
    }

    @Override
    public int login(User user) {
        return withConnection(conn -> {
            if (userTable.login(conn, user) > 0) {
                // Initial user creation or user update due to login, give the user an initial balance if they do
                // not already have a balance.
                UserBalance initialBalance = new UserBalance().setUserId(user.getId()).setBalance(USER_INITIAL_BALANCE.getInt());
                userBalanceTable.setInitialBalance(conn, initialBalance);
                return 1;
            }
            return 0;
        });
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
    public int delete(String id) {
        return withConnection(conn -> userTable.delete(conn, id));
    }
}
