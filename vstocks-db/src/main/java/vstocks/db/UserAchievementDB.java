package vstocks.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserAchievement;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public interface UserAchievementDB {
    Optional<UserAchievement> get(String userId, String achievementId);

    List<UserAchievement> getForUser(String userId);

    Results<UserAchievement> getForAchievement(String achievementId, Page page, Set<Sort> sort);

    Results<UserAchievement> getAll(Page page, Set<Sort> sort);

    int consume(Consumer<UserAchievement> consumer, Set<Sort> sort);

    int add(UserAchievement userAchievement);

    int deleteForUser(String userId);

    int delete(String userId, String achievementId);
}
