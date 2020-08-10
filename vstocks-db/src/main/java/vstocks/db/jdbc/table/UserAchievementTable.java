package vstocks.db.jdbc.table;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserAchievement;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Sort.SortDirection.DESC;

public class UserAchievementTable extends BaseTable {
    private static final RowMapper<UserAchievement> ROW_MAPPER = rs ->
            new UserAchievement()
                    .setUserId(rs.getString("user_id"))
                    .setAchievementId(rs.getString("achievement_id"))
                    .setTimestamp(rs.getTimestamp("timestamp").toInstant())
                    .setDescription(rs.getString("description"));

    private static final RowSetter<UserAchievement> INSERT_ROW_SETTER = (ps, userAchievement) -> {
        int index = 0;
        ps.setString(++index, userAchievement.getUserId());
        ps.setString(++index, userAchievement.getAchievementId());
        ps.setTimestamp(++index, Timestamp.from(userAchievement.getTimestamp()));
        ps.setString(++index, userAchievement.getDescription());
    };

    @Override
    protected Set<Sort> getDefaultSort() {
        return new HashSet<>(asList(TIMESTAMP.toSort(DESC), USER_ID.toSort(), ACHIEVEMENT_ID.toSort()));
    }

    public Optional<UserAchievement> get(Connection connection, String userId, String achievementId) {
        String sql = "SELECT * FROM user_achievements WHERE user_id = ? AND achievement_id = ?";
        return getOne(connection, ROW_MAPPER, sql, userId, achievementId);
    }

    public List<UserAchievement> getForUser(Connection connection, String userId) {
        String sql = "SELECT * FROM user_achievements WHERE user_id = ? ORDER BY timestamp DESC";
        return getList(connection, ROW_MAPPER, sql, userId);
    }

    public Results<UserAchievement> getForAchievement(Connection connection, String achievementId, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM user_achievements WHERE achievement_id = ? %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM user_achievements WHERE achievement_id = ?";
        return results(connection, ROW_MAPPER, page, sql, count, achievementId);
    }

    public Results<UserAchievement> getAll(Connection connection, Page page, Set<Sort> sort) {
        String sql = format("SELECT * FROM user_achievements %s LIMIT ? OFFSET ?", getSort(sort));
        String count = "SELECT COUNT(*) FROM user_achievements";
        return results(connection, ROW_MAPPER, page, sql, count);
    }

    public int consume(Connection connection, Consumer<UserAchievement> consumer, Set<Sort> sort) {
        String sql = format("SELECT * FROM user_achievements %s", getSort(sort));
        return consume(connection, ROW_MAPPER, consumer, sql);
    }

    public int add(Connection connection, UserAchievement userAchievement) {
        String sql = "INSERT INTO user_achievements (user_id, achievement_id, timestamp, description) VALUES (?, ?, ?, ?)";
        return update(connection, INSERT_ROW_SETTER, sql, userAchievement);
    }

    public int deleteForUser(Connection connection, String userId) {
        return update(connection, "DELETE FROM user_achievements WHERE user_id = ?", userId);
    }

    public int delete(Connection connection, String userId, String achievementId) {
        String sql = "DELETE FROM user_achievements WHERE user_id = ? AND achievement_id = ?";
        return update(connection, sql, userId, achievementId);
    }

    public int truncate(Connection connection) {
        return update(connection, "DELETE FROM user_achievements");
    }
}
