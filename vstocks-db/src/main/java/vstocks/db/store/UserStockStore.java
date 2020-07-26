package vstocks.db.store;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.UserStock;

import java.sql.Connection;
import java.util.Optional;

public interface UserStockStore {
    Optional<UserStock> get(Connection connection, String userId, String marketId, String stockId);

    Results<UserStock> getForUser(Connection connection, String userId, Page page);

    Results<UserStock> getForStock(Connection connection, String stockId, Page page);

    Results<UserStock> getAll(Connection connection, Page page);

    int add(Connection connection, UserStock userStock);

    int update(Connection connection, String userId, String marketId, String stockId, int delta);

    int delete(Connection connection, String userId, String marketId, String stockId);

    int truncate(Connection connection);
}
