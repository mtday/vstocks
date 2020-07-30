package vstocks.service.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.UserStock;

import java.util.Optional;
import java.util.function.Consumer;

public interface UserStockService {
    Optional<UserStock> get(String userId, String marketId, String stockId);

    Results<UserStock> getForUser(String userId, Page page);

    Results<UserStock> getForStock(String stockId, Page page);

    Results<UserStock> getAll(Page page);

    int consume(Consumer<UserStock> consumer);

    int buyStock(String userId, String marketId, String stockId, int shares);

    int sellStock(String userId, String marketId, String stockId, int shares);

    int add(UserStock userStock);

    int update(String userId, String marketId, String stockId, int delta);

    int delete(String userId, String marketId, String stockId);
}
