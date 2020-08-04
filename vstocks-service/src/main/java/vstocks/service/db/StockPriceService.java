package vstocks.service.db;

import vstocks.model.*;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface StockPriceService {
    Optional<StockPrice> getLatest(Market market, String symbol);

    Results<StockPrice> getLatest(Market market, Collection<String> symbol, Page page, Set<Sort> sort);

    Results<StockPrice> getForStock(Market market, String symbol, Page page, Set<Sort> sort);

    Results<StockPrice> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<StockPrice> consumer, Set<Sort> sort);

    int add(StockPrice stockPrice);

    int ageOff(Instant cutoff);
}
