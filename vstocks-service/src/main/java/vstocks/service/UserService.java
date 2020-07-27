package vstocks.service;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;

import java.util.Optional;

public interface UserService {
    boolean usernameExists(String username);

    Optional<User> get(String id);

    int login(User user);

    Results<User> getAll(Page page);

    int add(User user);

    int update(User user);

    int delete(String id);
}
