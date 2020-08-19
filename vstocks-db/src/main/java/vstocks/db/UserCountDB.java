package vstocks.db;

import vstocks.model.*;

import java.time.Instant;
import java.util.Set;

public interface UserCountDB {
    UserCount generateTotal();

    UserCount generateActive();

    UserCount getLatestTotal();

    UserCount getLatestActive();

    Results<UserCount> getAllTotal(Page page, Set<Sort> sort);

    Results<UserCount> getAllActive(Page page, Set<Sort> sort);

    int addTotal(UserCount userCount);

    int addActive(UserCount userCount);

    int ageOffTotal(Instant cutoff);

    int ageOffActive(Instant cutoff);
}
