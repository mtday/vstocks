package vstocks.db;

import vstocks.model.*;

import java.util.List;
import java.util.Optional;

public interface PricedUserStockService {
    Optional<PricedUserStock> get(String userId, Market market, String symbol);

    Results<PricedUserStock> getForUser(String userId, Page page, List<Sort> sort);

    Results<PricedUserStock> getForStock(Market market, String symbol, Page page, List<Sort> sort);

    Results<PricedUserStock> getAll(Page page, List<Sort> sort);
}
