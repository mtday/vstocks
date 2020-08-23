package vstocks.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserCredits;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface UserCreditsService {
    Optional<UserCredits> get(String userId);

    Results<UserCredits> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<UserCredits> consumer, Set<Sort> sort);

    int setInitialCredits(UserCredits initialCredits);

    int add(UserCredits userCredits);

    int update(String userId, long delta);

    int delete(String userId);

    int truncate();
}
