package vstocks.db;

import vstocks.model.*;

import java.util.Set;
import java.util.function.Consumer;

public interface OwnedStockService {
    int consumeForMarket(Market market, Consumer<Stock> consumer, Set<Sort> sort);

    int consume(Consumer<Stock> consumer, Set<Sort> sort);
}
