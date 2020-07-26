package vstocks.db.service.impl;

import vstocks.db.service.UserService;
import vstocks.db.store.UserStore;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;

import javax.sql.DataSource;
import java.util.Optional;

public class DefaultUserService extends BaseService implements UserService {
    private final UserStore userStore;

    public DefaultUserService(DataSource dataSource, UserStore userStore) {
        super(dataSource);
        this.userStore = userStore;
    }

    @Override
    public Optional<User> get(String id) {
        return withConnection(conn -> userStore.get(conn, id));
    }

    @Override
    public Optional<User> login(String login, String hashedPass) {
        return withConnection(conn -> userStore.login(conn, login, hashedPass));
    }

    @Override
    public Results<User> getAll(Page page) {
        return withConnection(conn -> userStore.getAll(conn, page));
    }

    @Override
    public int add(User user) {
        return withConnection(conn -> userStore.add(conn, user));
    }

    @Override
    public int update(User user) {
        return withConnection(conn -> userStore.update(conn, user));
    }

    @Override
    public int updatePassword(User user) {
        return withConnection(conn -> userStore.updatePassword(conn, user));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> userStore.delete(conn, id));
    }
}
