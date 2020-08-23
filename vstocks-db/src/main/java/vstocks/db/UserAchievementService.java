package vstocks.db;

import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.UserAchievement;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public interface UserAchievementService {
    Optional<UserAchievement> get(String userId, String achievementId);

    List<UserAchievement> getForUser(String userId);

    Results<UserAchievement> getForAchievement(String achievementId, Page page, List<Sort> sort);

    Results<UserAchievement> getAll(Page page, List<Sort> sort);

    int consume(Consumer<UserAchievement> consumer, List<Sort> sort);

    int add(UserAchievement userAchievement);

    int deleteForUser(String userId);

    int delete(String userId, String achievementId);

    int truncate();
}
