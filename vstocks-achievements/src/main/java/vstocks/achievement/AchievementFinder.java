package vstocks.achievement;

import vstocks.db.DBFactory;
import vstocks.model.UserAchievement;

import java.util.Set;
import java.util.function.Consumer;

public interface AchievementFinder {
    int find(DBFactory dbFactory, Set<String> userIds, Consumer<UserAchievement> consumer);
}
