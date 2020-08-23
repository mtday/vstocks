package vstocks.db.system;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.TotalUserCount;
import vstocks.model.system.TotalUserCountCollection;

import java.time.Instant;
import java.util.List;

public interface TotalUserCountService {
    int generate();

    TotalUserCountCollection getLatest();

    Results<TotalUserCount> getAll(Page page, List<Sort> sort);

    int add(TotalUserCount totalUserCount);

    int ageOff(Instant cutoff);

    int truncate();
}
