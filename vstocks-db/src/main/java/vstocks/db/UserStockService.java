package vstocks.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface UserStockService {
    Optional<UserStock> get(String userId, Market market, String symbol);

    Results<UserStock> getForUser(String userId, Page page, Set<Sort> sort);

    Results<UserStock> getForStock(Market market, String symbol, Page page, Set<Sort> sort);

    Results<UserStock> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<UserStock> consumer, Set<Sort> sort);

    int buyStock(String userId, Market market, String symbol, int shares);

    int sellStock(String userId, Market market, String symbol, int shares);

    int add(UserStock userStock);

    int update(String userId, Market market, String symbol, int delta);

    int deleteForUser(String userId);

    int delete(String userId, Market market, String symbol);

    int truncate();
}
