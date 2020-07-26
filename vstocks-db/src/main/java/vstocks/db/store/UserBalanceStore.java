package vstocks.db.store;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.UserBalance;

import java.sql.Connection;
import java.util.Optional;

public interface UserBalanceStore {
    Optional<UserBalance> get(Connection connection, String userId);

    Results<UserBalance> getAll(Connection connection, Page page);

    int add(Connection connection, UserBalance userBalance);

    int update(Connection connection, String userId, int delta);

    int delete(Connection connection, String userId);

    int truncate(Connection connection);
}
