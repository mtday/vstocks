package vstocks.db;

import vstocks.model.*;

import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface UserAchievementDB {
    Optional<UserAchievement> get(String userId, String achievementId);

    Results<UserAchievement> getForUser(String userId, Page page, Set<Sort> sort);

    Results<UserAchievement> getForAchievement(String achievementId, Page page, Set<Sort> sort);

    Results<UserAchievement> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<UserAchievement> consumer, Set<Sort> sort);

    int add(UserAchievement userAchievement);

    int deleteForUser(String userId);

    int delete(String userId, String achievementId);
}
