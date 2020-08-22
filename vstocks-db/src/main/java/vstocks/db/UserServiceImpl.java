package vstocks.db;

import vstocks.model.*;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static vstocks.config.Config.USER_INITIAL_CREDITS;

public class UserServiceImpl extends BaseService implements UserService {
    private final UserDB userTable = new UserDB();
    private final UserCreditsDB userCreditsTable = new UserCreditsDB();
    private final UserStockDB userStockTable = new UserStockDB();
    private final UserAchievementDB userAchievementTable = new UserAchievementDB();
    private final ActivityLogDB activityLogTable = new ActivityLogDB();

    public UserServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean usernameExists(String username) {
        return withConnection(conn -> userTable.usernameExists(conn, username));
    }

    @Override
    public Optional<User> get(String id) {
        return withConnection(conn -> userTable.get(conn, id));
    }

    @Override
    public Results<User> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> userTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<User> consumer, Set<Sort> sort) {
        return withConnection(conn -> userTable.consume(conn, consumer, sort));
    }

    @Override
    public int reset(String id) {
        return withConnection(conn -> {
            userCreditsTable.delete(conn, id);
            UserCredits initialCredits = new UserCredits().setUserId(id).setCredits(USER_INITIAL_CREDITS.getInt());
            userCreditsTable.setInitialCredits(conn, initialCredits);
            userStockTable.deleteForUser(conn, id);
            userAchievementTable.deleteForUser(conn, id);
            activityLogTable.deleteForUser(conn, id);
            return 1;
        });
    }

    @Override
    public int add(User user) {
        return withConnection(conn -> {
            if (userTable.add(conn, user) > 0) {
                // Initial user creation, give the user some initial credits.
                UserCredits initialCredits = new UserCredits().setUserId(user.getId()).setCredits(USER_INITIAL_CREDITS.getInt());
                userCreditsTable.setInitialCredits(conn, initialCredits);
                return 1;
            }
            return 0;
        });
    }

    @Override
    public int update(User user) {
        return withConnection(conn -> userTable.update(conn, user));
    }

    @Override
    public int delete(String id) {
        return withConnection(conn -> userTable.delete(conn, id));
    }

    @Override
    public int truncate() {
        return withConnection(userTable::truncate);
    }
}
