package vstocks.db.system;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.TotalTransactionCount;
import vstocks.model.system.TotalTransactionCountCollection;

import java.time.Instant;
import java.util.List;

public interface TotalTransactionCountService {
    int generate();

    TotalTransactionCountCollection getLatest();

    Results<TotalTransactionCount> getAll(Page page, List<Sort> sort);

    int add(TotalTransactionCount totalTransactionCount);

    int ageOff(Instant cutoff);

    int truncate();
}
