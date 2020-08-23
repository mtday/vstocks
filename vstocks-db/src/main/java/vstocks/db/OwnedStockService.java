package vstocks.db;

import vstocks.model.Market;
import vstocks.model.Sort;
import vstocks.model.Stock;

import java.util.List;
import java.util.function.Consumer;

public interface OwnedStockService {
    int consumeForMarket(Market market, Consumer<Stock> consumer, List<Sort> sort);

    int consume(Consumer<Stock> consumer, List<Sort> sort);
}
