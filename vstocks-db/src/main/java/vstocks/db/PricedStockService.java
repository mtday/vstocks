package vstocks.db;

import vstocks.model.*;

import java.util.List;
import java.util.Optional;

public interface PricedStockService {
    Optional<PricedStock> get(Market market, String symbol);

    Results<PricedStock> getForMarket(Market market, Page page, List<Sort> sort);

    Results<PricedStock> getAll(Page page, List<Sort> sort);

    int add(PricedStock pricedStock);
}
