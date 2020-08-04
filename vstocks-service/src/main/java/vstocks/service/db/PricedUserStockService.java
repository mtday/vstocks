package vstocks.service.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface PricedUserStockService {
    Optional<PricedUserStock> get(String userId, Market market, String symbol);

    Results<PricedUserStock> getForUser(String userId, Page page, Set<Sort> sort);

    Results<PricedUserStock> getForStock(Market market, String symbol, Page page, Set<Sort> sort);

    Results<PricedUserStock> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<PricedUserStock> consumer, Set<Sort> sort);
}
