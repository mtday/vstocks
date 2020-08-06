package vstocks.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface StockDB {
    Optional<Stock> get(Market market, String symbol, Boolean active);

    Results<Stock> getForMarket(Market market, Boolean active, Page page, Set<Sort> sort);

    int consumeForMarket(Market market, Boolean active, Consumer<Stock> consumer, Set<Sort> sort);

    Results<Stock> getAll(Boolean active, Page page, Set<Sort> sort);

    int consume(Boolean active, Consumer<Stock> consumer, Set<Sort> sort);

    int add(Stock stock);

    int update(Stock stock);

    int delete(Market market, String symbol);
}
