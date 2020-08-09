package vstocks.achievement.impl.beginner;

import vstocks.achievement.AchievementProvider;
import vstocks.achievement.AchievementValidator;
import vstocks.db.DBFactory;
import vstocks.model.*;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static vstocks.model.AchievementCategory.BEGINNER;
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.ActivityType.STOCK_SELL;

public class FirstStockActivity implements AchievementProvider {
    static String getId(Market market, ActivityType activityType) {
        return "first_" + activityType.name().toLowerCase(ENGLISH) + "_" + market.name().toLowerCase(ENGLISH);
    }

    @Override
    public List<Entry<Achievement, AchievementValidator>> getAchievements() {
        return Arrays.stream(Market.values()).flatMap(market -> Stream.of(
                new FirstStockActivityValidator(getId(market, STOCK_BUY), market, STOCK_BUY),
                new FirstStockActivityValidator(getId(market, STOCK_SELL), market, STOCK_SELL)
        )).map(v -> new AbstractMap.SimpleEntry<>(v.getAchievement(), (AchievementValidator) v)).collect(toList());
    }

    static class FirstStockActivityValidator implements AchievementValidator {
        private final String achievementId;
        private final Market market;
        private final ActivityType activityType;

        public FirstStockActivityValidator(String achievementId, Market market, ActivityType activityType) {
            this.achievementId = achievementId;
            this.market = market;
            this.activityType = activityType;
        }

        public Achievement getAchievement() {
            String marketName = market.getDisplayName();
            String nameVerb = activityType == STOCK_BUY ? "Purchase" : "Sale";
            String descriptionVerb = activityType == STOCK_BUY ? "Buy (with credits)" : "Sell";
            return new Achievement()
                    .setId(achievementId)
                    .setName(format("First %s Stock %s", marketName, nameVerb))
                    .setCategory(BEGINNER)
                    .setOrder(activityType == STOCK_BUY ? 5 : 10) // put purchase achievements before sale achievements
                    .setDescription(format("%s one or more shares of any stock on the %s market for any price.",
                            descriptionVerb, marketName));
        }

        @Override
        public Optional<UserAchievement> validate(DBFactory dbFactory, ActivityLog activityLog) {
            if (activityLog.getType() == activityType && activityLog.getMarket() == market) {
                ActivityLogSearch search = new ActivityLogSearch()
                        .setUserIds(singletonList(activityLog.getUserId()))
                        .setTypes(singletonList(activityType))
                        .setMarkets(singletonList(market));
                if (dbFactory.getActivityLogDB().count(search) == 1) {
                    String presentTenseVerb = activityType == STOCK_BUY ? "purchase" : "sale";
                    String pastTenseVerb = activityType == STOCK_BUY ? "bought" : "sold";
                    String description = format("First %s stock %s achieved - %d shares of %s %s for %d credits each.",
                            market.getDisplayName(), presentTenseVerb, Math.abs(activityLog.getShares()),
                            activityLog.getSymbol(), pastTenseVerb, activityLog.getPrice());
                    UserAchievement userAchievement = new UserAchievement()
                            .setUserId(activityLog.getUserId())
                            .setAchievementId(achievementId)
                            .setTimestamp(activityLog.getTimestamp())
                            .setDescription(description);
                    return Optional.of(userAchievement);
                }
            }
            return empty();
        }
    }
}
