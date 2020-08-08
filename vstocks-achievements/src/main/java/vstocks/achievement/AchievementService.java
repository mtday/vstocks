package vstocks.achievement;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import vstocks.db.DBFactory;
import vstocks.model.Achievement;
import vstocks.model.ActivityLog;
import vstocks.model.UserAchievement;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class AchievementService {
    private final Map<Achievement, AchievementProvider> achievementProviders;

    public AchievementService() {
        Comparator<Achievement> achievementComparator = Comparator.comparing(Achievement::getCategory)
                .thenComparingInt(Achievement::getOrder)
                .thenComparing(Achievement::getName);
        achievementProviders = new TreeMap<>(achievementComparator);

        ConfigurationBuilder configuration = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClass(AchievementService.class))
                .setScanners(new SubTypesScanner());
        Reflections reflections = new Reflections(configuration);
        reflections.getSubTypesOf(AchievementProvider.class).stream()
                .map(this::createProvider)
                .forEach(provider -> achievementProviders.put(provider.getAchievement(), provider));
    }

    private AchievementProvider createProvider(Class<? extends AchievementProvider> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Set<Achievement> getAchievements() {
        return achievementProviders.keySet();
    }

    public List<UserAchievement> check(DBFactory dbFactory, ActivityLog activityLog) {
        return achievementProviders.values().stream()
                .map(provider -> provider.validate(dbFactory, activityLog))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }
}
