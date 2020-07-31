package vstocks.service.db;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.UserStock;

import java.util.Optional;
import java.util.function.Consumer;

public interface UserStockService {
    Optional<UserStock> get(String userId, Market market, String stockId);

    Results<UserStock> getForUser(String userId, Page page);

    Results<UserStock> getForStock(Market market, String symbol, Page page);

    Results<UserStock> getAll(Page page);

    int consume(Consumer<UserStock> consumer);

    int buyStock(String userId, Market market, String symbol, int shares);

    int sellStock(String userId, Market market, String symbol, int shares);

    int add(UserStock userStock);

    int update(String userId, Market market, String symbol, int delta);

    int delete(String userId, Market market, String symbol);
}
