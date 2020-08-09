package vstocks.achievement;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import vstocks.db.DBFactory;
import vstocks.model.Achievement;
import vstocks.model.UserAchievement;

import java.util.*;
import java.util.function.Consumer;

public class AchievementService {
    private final Map<Achievement, AchievementFinder> achievements;

    public AchievementService() {
        Comparator<Achievement> achievementComparator = Comparator.comparing(Achievement::getCategory)
                .thenComparingInt(Achievement::getOrder)
                .thenComparing(Achievement::getName);
        achievements = new TreeMap<>(achievementComparator);

        ConfigurationBuilder configuration = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClass(AchievementService.class))
                .setScanners(new SubTypesScanner());
        Reflections reflections = new Reflections(configuration);
        reflections.getSubTypesOf(AchievementProvider.class).stream()
                .map(this::createProvider)
                .map(AchievementProvider::getAchievements)
                .flatMap(Collection::stream)
                .forEach(entry -> achievements.put(entry.getKey(), entry.getValue()));
    }

    private AchievementProvider createProvider(Class<? extends AchievementProvider> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Achievement> getAchievements() {
        return achievements.keySet();
    }

    public int find(DBFactory dbFactory, Set<String> userIds, Consumer<UserAchievement> consumer) {
        return achievements.values().stream()
                .mapToInt(finder -> finder.find(dbFactory, userIds, consumer))
                .sum();
    }
}
