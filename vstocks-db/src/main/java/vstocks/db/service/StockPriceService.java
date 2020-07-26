package vstocks.db.service;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.StockPrice;

import java.util.Collection;
import java.util.Optional;

public interface StockPriceService {
    Optional<StockPrice> get(String id);

    Results<StockPrice> getLatest(Collection<String> stockIds, Page page);

    Results<StockPrice> getForStock(String stockId, Page page);

    Results<StockPrice> getAll(Page page);

    int add(StockPrice stockPrice);

    int delete(String id);
}
