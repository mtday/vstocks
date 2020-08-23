package vstocks.db.system;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.ActiveTransactionCount;
import vstocks.model.system.ActiveTransactionCountCollection;

import java.time.Instant;
import java.util.List;

public interface ActiveTransactionCountService {
    int generate();

    ActiveTransactionCountCollection getLatest();

    Results<ActiveTransactionCount> getAll(Page page, List<Sort> sort);

    int add(ActiveTransactionCount activeTransactionCount);

    int ageOff(Instant cutoff);

    int truncate();
}
