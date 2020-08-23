package vstocks.db;

import vstocks.model.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface StockService {
    Optional<Stock> get(Market market, String symbol);

    Results<Stock> getForMarket(Market market, Page page, List<Sort> sort);

    int consumeForMarket(Market market, Consumer<Stock> consumer, List<Sort> sort);

    Results<Stock> getAll(Page page, List<Sort> sort);

    int consume(Consumer<Stock> consumer, List<Sort> sort);

    int add(Stock stock);

    int update(Stock stock);

    int delete(Market market, String symbol);

    int truncate();
}
