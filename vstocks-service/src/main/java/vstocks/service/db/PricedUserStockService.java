package vstocks.service.db;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.PricedUserStock;
import vstocks.model.Results;

import java.util.Optional;
import java.util.function.Consumer;

public interface PricedUserStockService {
    Optional<PricedUserStock> get(String userId, Market market, String symbol);

    Results<PricedUserStock> getForUser(String userId, Page page);

    Results<PricedUserStock> getForStock(Market market, String symbol, Page page);

    Results<PricedUserStock> getAll(Page page);

    int consume(Consumer<PricedUserStock> consumer);
}
