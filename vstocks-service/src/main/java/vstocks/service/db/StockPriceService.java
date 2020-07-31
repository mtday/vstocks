package vstocks.service.db;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.StockPrice;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public interface StockPriceService {
    Optional<StockPrice> getLatest(Market market, String symbol);

    Results<StockPrice> getLatest(Market market, Collection<String> symbol, Page page);

    Results<StockPrice> getForStock(Market market, String symbol, Page page);

    Results<StockPrice> getAll(Page page);

    int consume(Consumer<StockPrice> consumer);

    int add(StockPrice stockPrice);

    int ageOff(Instant cutoff);
}
