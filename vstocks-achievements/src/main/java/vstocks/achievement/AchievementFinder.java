package vstocks.achievement;

import vstocks.db.ServiceFactory;
import vstocks.model.UserAchievement;

import java.util.Set;
import java.util.function.Consumer;

public interface AchievementFinder {
    int find(ServiceFactory dbFactory, Set<String> userIds, Consumer<UserAchievement> consumer);
}
