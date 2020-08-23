package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalValue;
import vstocks.model.portfolio.MarketTotalValueCollection;

import java.time.Instant;
import java.util.Set;

public interface MarketTotalValueService {
    int generate();

    MarketTotalValueCollection getLatest(String userId);

    Results<MarketTotalValue> getAll(Page page, Set<Sort> sort);

    int add(MarketTotalValue marketTotalValue);

    int ageOff(Instant cutoff);

    int truncate();
}
