package vstocks.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserBalance;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface UserBalanceDB {
    Optional<UserBalance> get(String userId);

    Results<UserBalance> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<UserBalance> consumer, Set<Sort> sort);

    int setInitialBalance(UserBalance initialBalance);

    int add(UserBalance userBalance);

    int update(String userId, int delta);

    int delete(String userId);
}
