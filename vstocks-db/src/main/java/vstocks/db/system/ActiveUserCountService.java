package vstocks.db.system;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.system.ActiveUserCount;
import vstocks.model.system.ActiveUserCountCollection;

import java.time.Instant;
import java.util.List;

public interface ActiveUserCountService {
    int generate();

    ActiveUserCountCollection getLatest();

    Results<ActiveUserCount> getAll(Page page, List<Sort> sort);

    int add(ActiveUserCount activeUserCount);

    int ageOff(Instant cutoff);

    int truncate();
}
