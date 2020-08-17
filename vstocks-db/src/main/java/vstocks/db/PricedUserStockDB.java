package vstocks.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.Set;

public interface PricedUserStockDB {
    Optional<PricedUserStock> get(String userId, Market market, String symbol);

    Results<PricedUserStock> getForUser(String userId, Page page, Set<Sort> sort);

    Results<PricedUserStock> getForStock(Market market, String symbol, Page page, Set<Sort> sort);

    Results<PricedUserStock> getAll(Page page, Set<Sort> sort);
}
