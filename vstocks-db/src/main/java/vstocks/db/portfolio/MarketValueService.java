package vstocks.db.portfolio;

import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketValue;
import vstocks.model.portfolio.MarketValueCollection;
import vstocks.model.portfolio.ValuedUser;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface MarketValueService {
    long setCurrentBatch(long batch);

    int generate();

    MarketValueCollection getLatest(String userId, Market market);

    Map<Market, MarketValueCollection> getLatest(String userId);

    Results<MarketValue> getAll(Market market, Page page, List<Sort> sort);

    Results<ValuedUser> getUsers(Market market, Page page);

    int add(MarketValue marketValue);

    int ageOff(Instant cutoff);

    int truncate();
}
