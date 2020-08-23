package vstocks.db.portfolio;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketValue;
import vstocks.model.portfolio.MarketValueCollection;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public interface MarketValueService {
    int generate(Market market);

    MarketValueCollection getLatest(String userId, Market market);

    Map<Market, MarketValueCollection> getLatest(String userId);

    Results<MarketValue> getAll(Market market, Page page, Set<Sort> sort);

    int add(MarketValue marketValue);

    int ageOff(Instant cutoff);

    int truncate();
}
