package vstocks.service.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.UserBalance;

import java.util.Optional;
import java.util.function.Consumer;

public interface UserBalanceService {
    Optional<UserBalance> get(String userId);

    Results<UserBalance> getAll(Page page);

    int consume(Consumer<UserBalance> consumer);

    int setInitialBalance(UserBalance initialBalance);

    int add(UserBalance userBalance);

    int update(String userId, int delta);

    int delete(String userId);
}
