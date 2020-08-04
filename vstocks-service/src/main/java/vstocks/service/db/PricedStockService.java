package vstocks.service.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface PricedStockService {
    Optional<PricedStock> get(Market market, String symbol);

    Results<PricedStock> getForMarket(Market market, Page page, Set<Sort> sort);

    int consumeForMarket(Market market, Consumer<PricedStock> consumer, Set<Sort> sort);

    Results<PricedStock> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<PricedStock> consumer, Set<Sort> sort);

    int add(PricedStock pricedStock);
}
