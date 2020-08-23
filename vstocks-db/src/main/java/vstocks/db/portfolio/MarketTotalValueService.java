package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.MarketTotalValue;
import vstocks.model.portfolio.MarketTotalValueCollection;
import vstocks.model.portfolio.ValuedUser;

import java.time.Instant;
import java.util.List;

public interface MarketTotalValueService {
    long setCurrentBatch(long batch);

    int generate();

    MarketTotalValueCollection getLatest(String userId);

    Results<MarketTotalValue> getAll(Page page, List<Sort> sort);

    Results<ValuedUser> getUsers(Page page);

    int add(MarketTotalValue marketTotalValue);

    int ageOff(Instant cutoff);

    int truncate();
}
