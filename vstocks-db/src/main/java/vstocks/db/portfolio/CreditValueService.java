package vstocks.db.portfolio;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.portfolio.CreditValue;
import vstocks.model.portfolio.CreditValueCollection;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public interface CreditValueService {
    int generate(Consumer<CreditValue> consumer);

    CreditValueCollection getLatest(String userId);

    Results<CreditValue> getAll(Page page, Set<Sort> sort);

    int add(CreditValue creditValue);

    int addAll(Collection<CreditValue> creditValues);

    int ageOff(Instant cutoff);

    int truncate();
}
