package vstocks.achievement;

import vstocks.db.DBFactory;
import vstocks.model.ActivityLog;
import vstocks.model.UserAchievement;

import java.util.Optional;

public interface AchievementValidator {
    Optional<UserAchievement> validate(DBFactory dbFactory, ActivityLog activityLog);
}
