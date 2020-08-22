package vstocks.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.Set;

public interface PricedStockService {
    Optional<PricedStock> get(Market market, String symbol);

    Results<PricedStock> getForMarket(Market market, Page page, Set<Sort> sort);

    Results<PricedStock> getAll(Page page, Set<Sort> sort);

    int add(PricedStock pricedStock);
}
