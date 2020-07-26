package vstocks.service.jdbc;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.UserBalance;
import vstocks.service.UserBalanceService;
import vstocks.service.jdbc.table.UserBalanceTable;

import javax.sql.DataSource;
import java.util.Optional;

public class JdbcUserBalanceService extends BaseService implements UserBalanceService {
    private final UserBalanceTable userBalanceTable = new UserBalanceTable();

    public JdbcUserBalanceService(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<UserBalance> get(String userId) {
        return withConnection(conn -> userBalanceTable.get(conn, userId));
    }

    @Override
    public Results<UserBalance> getAll(Page page) {
        return withConnection(conn -> userBalanceTable.getAll(conn, page));
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
