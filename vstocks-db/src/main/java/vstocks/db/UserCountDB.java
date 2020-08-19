package vstocks.db;

import vstocks.model.*;

import java.time.Instant;
import java.util.Set;

public interface UserCountDB {
    UserCount generate();

    UserCount getLatest();

    Results<UserCount> getAll(Page page, Set<Sort> sort);

    int add(UserCount userCount);

    int ageOff(Instant cutoff);
}
