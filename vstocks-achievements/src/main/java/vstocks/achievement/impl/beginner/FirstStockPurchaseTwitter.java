package vstocks.achievement.impl.beginner;

import vstocks.achievement.AchievementProvider;
import vstocks.db.DBFactory;
import vstocks.model.*;

import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static vstocks.model.AchievementCategory.BEGINNER;
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Sort.SortDirection.DESC;

public class FirstStockPurchaseTwitter implements AchievementProvider {
    static final String ID = "first-stock-purchase-twitter";

    @Override
    public Achievement getAchievement() {
        return new Achievement()
                .setId(ID)
                .setName(format("First Stock Purchase (%s)", TWITTER.getDisplayName()))
                .setCategory(BEGINNER)
                .setOrder(5)
                .setDescription(format("Buy one or more shares of any stock on the %s market for any price.",
                        TWITTER.getDisplayName()));
    }

    @Override
    public Optional<UserAchievement> validate(DBFactory dbFactory, ActivityLog activityLog) {
        if (activityLog.getType() == STOCK_BUY && activityLog.getMarket() == TWITTER) {
            Page page = new Page().setPage(1).setSize(1);
            Set<Sort> sort = singleton(TIMESTAMP.toSort(DESC));
            ActivityLogSearch search = new ActivityLogSearch()
                    .setUserIds(singletonList(activityLog.getUserId()))
                    .setTypes(singletonList(STOCK_BUY))
                    .setMarkets(singletonList(TWITTER));
            if (dbFactory.getActivityLogDB().count(search) == 1) {
                String description = format("First stock purchase completed - %d shares of %s/%s bought for \u22bb%d each",
                        activityLog.getShares(), activityLog.getMarket().name(), activityLog.getSymbol(),
                        activityLog.getPrice());
                UserAchievement userAchievement = new UserAchievement()
                        .setUserId(activityLog.getUserId())
                        .setAchievementId(ID)
                        .setTimestamp(activityLog.getTimestamp())
                        .setDescription(description);
                return Optional.of(userAchievement);
            }
        }
        return Optional.empty();
    }
}
