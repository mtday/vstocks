package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditValue;
import vstocks.model.portfolio.CreditValueCollection;
import vstocks.model.portfolio.ValuedUser;

import java.time.Instant;
import java.util.List;

public interface CreditValueService {
    long setCurrentBatch(long batch);

    int generate();

    CreditValueCollection getLatest(String userId);

    Results<CreditValue> getAll(Page page, List<Sort> sort);

    Results<ValuedUser> getUsers(Page page);

    int add(CreditValue creditValue);

    int ageOff(Instant cutoff);

    int truncate();
}
