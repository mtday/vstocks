package vstocks.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface PricedStockDB {
    Optional<PricedStock> get(Market market, String symbol, Boolean active);

    Results<PricedStock> getForMarket(Market market, Boolean active, Page page, Set<Sort> sort);

    int consumeForMarket(Market market, Boolean active, Consumer<PricedStock> consumer, Set<Sort> sort);

    Results<PricedStock> getAll(Boolean active, Page page, Set<Sort> sort);

    int consume(Boolean active, Consumer<PricedStock> consumer, Set<Sort> sort);

    int add(PricedStock pricedStock);
}
