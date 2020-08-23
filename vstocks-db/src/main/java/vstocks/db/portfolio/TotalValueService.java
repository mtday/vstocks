package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.TotalValue;
import vstocks.model.portfolio.TotalValueCollection;
import vstocks.model.portfolio.ValuedUser;

import java.time.Instant;
import java.util.List;

public interface TotalValueService {
    long setCurrentBatch(long batch);

    int generate();

    TotalValueCollection getLatest(String userId);

    Results<TotalValue> getAll(Page page, List<Sort> sort);

    Results<ValuedUser> getUsers(Page page);

    int add(TotalValue totalValue);

    int ageOff(Instant cutoff);

    int truncate();
}
