package vstocks.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface StockService {
    Optional<Stock> get(Market market, String symbol);

    Results<Stock> getForMarket(Market market, Page page, Set<Sort> sort);

    int consumeForMarket(Market market, Consumer<Stock> consumer, Set<Sort> sort);

    Results<Stock> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<Stock> consumer, Set<Sort> sort);

    int add(Stock stock);

    int update(Stock stock);

    int delete(Market market, String symbol);

    int truncate();
}
