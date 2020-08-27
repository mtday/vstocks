package vstocks.db;

import vstocks.model.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface UserStockService {
    Optional<UserStock> get(String userId, Market market, String symbol);

    Results<UserStock> getForUser(String userId, Page page, List<Sort> sort);

    Results<UserStock> getForStock(Market market, String symbol, Page page, List<Sort> sort);

    Results<UserStock> getAll(Page page, List<Sort> sort);

    int consume(Consumer<UserStock> consumer, List<Sort> sort);

    int buyStock(String userId, Market market, String symbol, long shares);

    int sellStock(String userId, Market market, String symbol, long shares);

    int add(UserStock userStock);

    int update(String userId, Market market, String symbol, long delta);

    int deleteForUser(String userId);

    int delete(String userId, Market market, String symbol);

    int truncate();
}
