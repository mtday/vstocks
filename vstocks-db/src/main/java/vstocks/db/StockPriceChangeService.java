package vstocks.db;

import vstocks.model.*;

import java.time.Instant;
import java.util.List;

public interface StockPriceChangeService {
    long setCurrentBatch(long batch);

    int generate();

    StockPriceChangeCollection getLatest(Market market, String symbol);

    Results<StockPriceChange> getForMarket(Market market, Page page, List<Sort> sort);

    Results<StockPriceChange> getAll(Page page, List<Sort> sort);

    int add(StockPriceChange stockPriceChange);

    int ageOff(Instant cutoff);

    int truncate();
}
