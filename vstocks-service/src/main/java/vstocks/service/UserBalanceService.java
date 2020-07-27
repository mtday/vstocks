package vstocks.service;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.UserBalance;

import java.util.Optional;

public interface UserBalanceService {
    Optional<UserBalance> get(String userId);

    Results<UserBalance> getAll(Page page);

    int setInitialBalance(UserBalance initialBalance);

    int add(UserBalance userBalance);

    int update(String userId, int delta);

    int delete(String userId);
}
