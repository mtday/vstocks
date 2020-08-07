package vstocks.db.jdbc;

import vstocks.db.UserDB;
import vstocks.db.jdbc.table.ActivityLogTable;
import vstocks.db.jdbc.table.UserBalanceTable;
import vstocks.db.jdbc.table.UserStockTable;
import vstocks.db.jdbc.table.UserTable;
import vstocks.model.*;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static vstocks.config.Config.USER_INITIAL_BALANCE;

public class JdbcUserDB extends BaseService implements UserDB {
    private final UserTable userTable = new UserTable();
    private final UserBalanceTable userBalanceTable = new UserBalanceTable();
    private final UserStockTable userStockTable = new UserStockTable();
    private final ActivityLogTable activityLogTable = new ActivityLogTable();

    public JdbcUserDB(DataSource dataSource) {
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
    public Results<User> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> userTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<User> consumer, Set<Sort> sort) {
        return withConnection(conn -> userTable.consume(conn, consumer, sort));
    }

    @Override
    public int reset(String id) {
        return withConnection(conn -> {
            userBalanceTable.delete(conn, id);
            UserBalance initialBalance = new UserBalance().setUserId(id).setBalance(USER_INITIAL_BALANCE.getInt());
            userBalanceTable.setInitialBalance(conn, initialBalance);
            userStockTable.deleteForUser(conn, id);
            activityLogTable.deleteForUser(conn, id);
            return 1;
        });
    }

    @Override
    public int add(User user) {
        return withConnection(conn -> {
            if (userTable.add(conn, user) > 0) {
                // Initial user creation, give the user an initial balance.
                UserBalance initialBalance = new UserBalance().setUserId(user.getId()).setBalance(USER_INITIAL_BALANCE.getInt());
                userBalanceTable.setInitialBalance(conn, initialBalance);
                return 1;
            }
            return 0;
        });
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
