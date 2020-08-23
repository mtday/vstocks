package vstocks.db.system;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.ActiveTransactionCount;
import vstocks.model.system.ActiveTransactionCountCollection;

import java.time.Instant;
import java.util.Set;

public interface ActiveTransactionCountService {
    int generate();

    ActiveTransactionCountCollection getLatest();

    Results<ActiveTransactionCount> getAll(Page page, Set<Sort> sort);

    int add(ActiveTransactionCount activeTransactionCount);

    int ageOff(Instant cutoff);

    int truncate();
}
