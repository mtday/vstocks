package vstocks.db.jdbc;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserBalance;
import vstocks.db.UserBalanceDB;
import vstocks.db.jdbc.table.UserBalanceTable;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class JdbcUserBalanceDB extends BaseService implements UserBalanceDB {
    private final UserBalanceTable userBalanceTable = new UserBalanceTable();

    public JdbcUserBalanceDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<UserBalance> get(String userId) {
        return withConnection(conn -> userBalanceTable.get(conn, userId));
    }

    @Override
    public Results<UserBalance> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> userBalanceTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<UserBalance> consumer, Set<Sort> sort) {
        return withConnection(conn -> userBalanceTable.consume(conn, consumer, sort));
    }

    @Override
    public int setInitialBalance(UserBalance initialBalance) {
        return withConnection(conn -> userBalanceTable.setInitialBalance(conn, initialBalance));
    }

    @Override
    public int add(UserBalance userBalance) {
        return withConnection(conn -> userBalanceTable.add(conn, userBalance));
    }

    @Override
    public int update(String userId, int delta) {
        return withConnection(conn -> userBalanceTable.update(conn, userId, delta));
    }

    @Override
    public int delete(String userId) {
        return withConnection(conn -> userBalanceTable.delete(conn, userId));
    }
}
