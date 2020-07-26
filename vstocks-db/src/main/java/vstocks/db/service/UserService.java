package vstocks.db.service;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;

import java.util.Optional;

public interface UserService {
    Optional<User> get(String id);

    Optional<User> login(String login, String hashedPass);

    Results<User> getAll(Page page);

    int add(User user);

    int update(User user);

    int updatePassword(User user);

    int delete(String id);
}
