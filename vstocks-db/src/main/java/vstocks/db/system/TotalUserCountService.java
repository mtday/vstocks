package vstocks.db.system;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.TotalUserCount;
import vstocks.model.system.TotalUserCountCollection;

import java.time.Instant;
import java.util.Set;

public interface TotalUserCountService {
    TotalUserCount generate();

    TotalUserCountCollection getLatest();

    Results<TotalUserCount> getAll(Page page, Set<Sort> sort);

    int add(TotalUserCount totalUserCount);

    int ageOff(Instant cutoff);

    int truncate();
}
