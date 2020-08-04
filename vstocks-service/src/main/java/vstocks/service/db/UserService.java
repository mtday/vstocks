package vstocks.service.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.User;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface UserService {
    boolean usernameExists(String username);

    Optional<User> get(String id);

    int login(User user);

    Results<User> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<User> consumer, Set<Sort> sort);

    int add(User user);

    int update(User user);

    int delete(String id);
}
