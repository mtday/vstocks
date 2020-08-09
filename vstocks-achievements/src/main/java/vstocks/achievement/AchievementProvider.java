package vstocks.achievement;

import vstocks.model.Achievement;

import java.util.List;
import java.util.Map.Entry;

public interface AchievementProvider {
    List<Entry<Achievement, AchievementFinder>> getAchievements();
}
