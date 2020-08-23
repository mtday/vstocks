package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditValue;
import vstocks.model.portfolio.CreditValueCollection;

import java.time.Instant;
import java.util.Set;

public interface CreditValueService {
    int generate();

    CreditValueCollection getLatest(String userId);

    Results<CreditValue> getAll(Page page, Set<Sort> sort);

    int add(CreditValue creditValue);

    int ageOff(Instant cutoff);

    int truncate();
}
