package vstocks.db.store;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;

import java.sql.Connection;
import java.util.Optional;

public interface UserStore {
    Optional<User> get(Connection connection, String id);

    Optional<User> login(Connection connection, String login, String hashedPass);

    Results<User> getAll(Connection connection, Page page);

    int add(Connection connection, User user);

    int update(Connection connection, User user);

    int updatePassword(Connection connection, User user);

    int delete(Connection connection, String id);

    int truncate(Connection connection);
}
