package vstocks.service;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;

import java.util.Optional;

public interface StockService {
    Optional<Stock> get(String marketId, String stockId);

    Results<Stock> getForMarket(String marketId, Page page);

    Results<Stock> getAll(Page page);

    int add(Stock stock);

    int update(Stock stock);

    int delete(String marketId, String stockId);
}
