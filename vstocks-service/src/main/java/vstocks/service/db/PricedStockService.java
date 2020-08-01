package vstocks.service.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.function.Consumer;

public interface PricedStockService {
    Optional<PricedStock> get(Market market, String symbol);

    Results<PricedStock> getForMarket(Market market, Page page);

    int consumeForMarket(Market market, Consumer<PricedStock> consumer);

    Results<PricedStock> getAll(Page page);

    int consume(Consumer<PricedStock> consumer);
}
