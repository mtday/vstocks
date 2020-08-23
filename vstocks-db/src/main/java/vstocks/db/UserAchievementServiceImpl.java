package vstocks.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserAchievement;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class UserAchievementServiceImpl extends BaseService implements UserAchievementService {
    private final UserAchievementDB userAchievementDB = new UserAchievementDB();

    public UserAchievementServiceImpl(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<UserAchievement> get(String userId, String achievementId) {
        return withConnection(conn -> userAchievementDB.get(conn, userId, achievementId));
    }

    @Override
    public List<UserAchievement> getForUser(String userId) {
        return withConnection(conn -> userAchievementDB.getForUser(conn, userId));
    }

    @Override
    public Results<UserAchievement> getForAchievement(String achievementId, Page page, List<Sort> sort) {
        return withConnection(conn -> userAchievementDB.getForAchievement(conn, achievementId, page, sort));
    }

    @Override
    public Results<UserAchievement> getAll(Page page, List<Sort> sort) {
        return withConnection(conn -> userAchievementDB.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<UserAchievement> consumer, List<Sort> sort) {
        return withConnection(conn -> userAchievementDB.consume(conn, consumer, sort));
    }

    @Override
    public int add(UserAchievement userAchievement) {
        return withConnection(conn -> userAchievementDB.add(conn, userAchievement));
    }

    @Override
    public int deleteForUser(String userId) {
        return withConnection(conn -> userAchievementDB.deleteForUser(conn, userId));
    }

    @Override
    public int delete(String userId, String achievementId) {
        return withConnection(conn -> userAchievementDB.delete(conn, userId, achievementId));
    }

    @Override
    public int truncate() {
        return withConnection(userAchievementDB::truncate);
    }
}
