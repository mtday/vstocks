package vstocks.db.jdbc;

import vstocks.db.UserAchievementDB;
import vstocks.db.jdbc.table.UserAchievementTable;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserAchievement;

import javax.sql.DataSource;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public class JdbcUserAchievementDB extends BaseService implements UserAchievementDB {
    private final UserAchievementTable userAchievementTable = new UserAchievementTable();

    public JdbcUserAchievementDB(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<UserAchievement> get(String userId, String achievementId) {
        return withConnection(conn -> userAchievementTable.get(conn, userId, achievementId));
    }

    @Override
    public Results<UserAchievement> getForUser(String userId, Page page, Set<Sort> sort) {
        return withConnection(conn -> userAchievementTable.getForUser(conn, userId, page, sort));
    }

    @Override
    public Results<UserAchievement> getForAchievement(String achievementId, Page page, Set<Sort> sort) {
        return withConnection(conn -> userAchievementTable.getForAchievement(conn, achievementId, page, sort));
    }

    @Override
    public Results<UserAchievement> getAll(Page page, Set<Sort> sort) {
        return withConnection(conn -> userAchievementTable.getAll(conn, page, sort));
    }

    @Override
    public int consume(Consumer<UserAchievement> consumer, Set<Sort> sort) {
        return withConnection(conn -> userAchievementTable.consume(conn, consumer, sort));
    }

    @Override
    public int add(UserAchievement userAchievement) {
        return withConnection(conn -> userAchievementTable.add(conn, userAchievement));
    }

    @Override
    public int deleteForUser(String userId) {
        return withConnection(conn -> userAchievementTable.deleteForUser(conn, userId));
    }

    @Override
    public int delete(String userId, String achievementId) {
        return withConnection(conn -> userAchievementTable.delete(conn, userId, achievementId));
    }
}
