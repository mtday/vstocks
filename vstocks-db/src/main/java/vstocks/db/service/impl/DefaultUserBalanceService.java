package vstocks.db.service.impl;

import vstocks.db.service.UserBalanceService;
import vstocks.db.store.UserBalanceStore;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.UserBalance;

import javax.sql.DataSource;
import java.util.Optional;

public class DefaultUserBalanceService extends BaseService implements UserBalanceService {
    private final UserBalanceStore userBalanceStore;

    public DefaultUserBalanceService(DataSource dataSource, UserBalanceStore userBalanceStore) {
        super(dataSource);
        this.userBalanceStore = userBalanceStore;
    }

    @Override
    public Optional<UserBalance> get(String userId) {
        return withConnection(conn -> userBalanceStore.get(conn, userId));
    }

    @Override
    public Results<UserBalance> getAll(Page page) {
        return withConnection(conn -> userBalanceStore.getAll(conn, page));
    }

    @Override
    public int add(UserBalance userBalance) {
        return withConnection(conn -> userBalanceStore.add(conn, userBalance));
    }

    @Override
    public int update(String userId, int delta) {
        return withConnection(conn -> userBalanceStore.update(conn, userId, delta));
    }

    @Override
    public int delete(String userId) {
        return withConnection(conn -> userBalanceStore.delete(conn, userId));
    }
}
