package vstocks.db;

import vstocks.model.*;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static vstocks.config.Config.USER_INITIAL_CREDITS;

public class UserServiceImpl extends BaseService implements UserService {
    private final UserDB userDB = new UserDB();
    private final UserCreditsDB userCreditsDB = new UserCreditsDB();
    private final UserStockDB userStockDB = new UserStockDB();
    private final UserAchievementDB userAchievementDB = new UserAchievementDB();
    private final ActivityLogDB activityLogDB = new ActivityLogDB();

    public UserServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean usernameExists(String username) {
        return withConnection(conn -> userDB.usernameExists(conn, username));
    }

    @Override
    public Optional<User> get(String id) {
        return withConnection(conn -> userDB.get(conn, id));
    }

    @Override
    public Results<User> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> userDB.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<User> consumer, List<Sort> sort) {
        return withConnection(conn -> userDB.consume(conn, consumer, sort));
    }

    @Override
    public int reset(String id) {
        return withConnection(conn -> {
            userCreditsDB.delete(conn, id);
            UserCredits initialCredits = new UserCredits().setUserId(id).setCredits(USER_INITIAL_CREDITS.getInt());
            userCreditsDB.setInitialCredits(conn, initialCredits);
            userStockDB.deleteForUser(conn, id);
            userAchievementDB.deleteForUser(conn, id);
            activityLogDB.deleteForUser(conn, id);
            return 1;
        });
    }

    @Override
    public int add(User user) {
        return withConnection(conn -> {
            if (userDB.add(conn, user) > 0) {
                // Initial user creation, give the user some initial credits.
                UserCredits initialCredits = new UserCredits().setUserId(user.getId()).setCredits(USER_INITIAL_CREDITS.getInt());
                userCreditsDB.setInitialCredits(conn, initialCredits);
                return 1;
            }
            return 0;
        });
    }

    @Override
    public int update(User user) {
        return withConnection(conn -> userDB.update(conn, user));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> userDB.delete(conn, id));
    }

    @Override
    public int truncate() {
        return withConnection(userDB::truncate);
    }
}
