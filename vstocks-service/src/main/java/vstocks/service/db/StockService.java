package vstocks.service.db;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;

import java.util.Optional;
import java.util.function.Consumer;

public interface StockService {
    Optional<Stock> get(Market market, String stockId);

    Results<Stock> getForMarket(Market market, Page page);

    int consumeForMarket(Market market, Consumer<Stock> consumer);

    Results<Stock> getAll(Page page);

    int consume(Consumer<Stock> consumer);

    int add(Stock stock);

    int update(Stock stock);

    int delete(Market market, String stockId);
}
