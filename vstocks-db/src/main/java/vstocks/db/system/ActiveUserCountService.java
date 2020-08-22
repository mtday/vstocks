package vstocks.db.system;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.ActiveUserCount;
import vstocks.model.system.ActiveUserCountCollection;

import java.time.Instant;
import java.util.Set;

public interface ActiveUserCountService {
    ActiveUserCount generate();

    ActiveUserCountCollection getLatest();

    Results<ActiveUserCount> getAll(Page page, Set<Sort> sort);

    int add(ActiveUserCount activeUserCount);

    int ageOff(Instant cutoff);

    int truncate();
}
