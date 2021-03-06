package vstocks.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.User;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface UserService {
    boolean usernameExists(String username);

    Optional<User> get(String id);

    Results<User> getAll(Page page, List<Sort> sort);

    int consume(Consumer<User> consumer, List<Sort> sort);

    int reset(String id);

    int add(User user);

    int update(User user);

    int delete(String id);

    int truncate();
}
