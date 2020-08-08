package vstocks.achievement.impl.beginner;

import org.junit.Test;
import vstocks.achievement.impl.BaseAchievementProviderIT;
import vstocks.model.Achievement;
import vstocks.model.ActivityLog;
import vstocks.model.UserAchievement;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static vstocks.model.AchievementCategory.BEGINNER;
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.ActivityType.STOCK_SELL;

public class FirstStockPurchaseTwitterIT extends BaseAchievementProviderIT {
    @Test
    public void testGetAchievement() {
        Achievement achievement = new FirstStockPurchaseTwitter().getAchievement();
        assertEquals(FirstStockPurchaseTwitter.ID, achievement.getId());
        assertEquals("First Stock Purchase (Twitter)", achievement.getName());
        assertEquals(BEGINNER, achievement.getCategory());
        assertEquals(5, achievement.getOrder());
        assertEquals("Buy one or more shares of any stock on the Twitter market for any price.",
                achievement.getDescription());
    }

    @Test
    public void testValidateWrongActivityLogType() {
        ActivityLog activityLog = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(STOCK_SELL)
                .setTimestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .setMarket(twitterStock.getMarket())
                .setSymbol(twitterStock.getSymbol())
                .setShares(-5)
                .setPrice(10);

        assertFalse(new FirstStockPurchaseTwitter().validate(getDBFactory(), activityLog).isPresent());
    }

    @Test
    public void testValidateWrongActivityLogMarket() {
        ActivityLog activityLog = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(STOCK_BUY)
                .setTimestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .setMarket(youtubeStock.getMarket())
                .setSymbol(youtubeStock.getSymbol())
                .setShares(5)
                .setPrice(10);

        assertFalse(new FirstStockPurchaseTwitter().validate(getDBFactory(), activityLog).isPresent());
    }

    @Test
    public void testValidateNoActivityLogs() {
        ActivityLog activityLog = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(STOCK_BUY)
                .setTimestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .setMarket(twitterStock.getMarket())
                .setSymbol(twitterStock.getSymbol())
                .setShares(5)
                .setPrice(10);

        assertFalse(new FirstStockPurchaseTwitter().validate(getDBFactory(), activityLog).isPresent());
    }

    @Test
    public void testValidateTooManyActivityLogs() {
        ActivityLog activityLog1 = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(STOCK_BUY)
                .setTimestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .setMarket(twitterStock.getMarket())
                .setSymbol(twitterStock.getSymbol())
                .setShares(5)
                .setPrice(10);
        ActivityLog activityLog2 = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(STOCK_BUY)
                .setTimestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .setMarket(twitterStock.getMarket())
                .setSymbol(twitterStock.getSymbol())
                .setShares(5)
                .setPrice(10);

        assertEquals(1, getDBFactory().getActivityLogDB().add(activityLog1));
        assertEquals(1, getDBFactory().getActivityLogDB().add(activityLog2));

        assertFalse(new FirstStockPurchaseTwitter().validate(getDBFactory(), activityLog1).isPresent());
    }

    @Test
    public void testValidateMatch() {
        ActivityLog activityLog = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(STOCK_BUY)
                .setTimestamp(Instant.now().truncatedTo(ChronoUnit.SECONDS))
                .setMarket(twitterStock.getMarket())
                .setSymbol(twitterStock.getSymbol())
                .setShares(5)
                .setPrice(10);

        assertEquals(1, getDBFactory().getActivityLogDB().add(activityLog));

        Optional<UserAchievement> userAchievement = new FirstStockPurchaseTwitter().validate(getDBFactory(), activityLog);
        assertTrue(userAchievement.isPresent());
        assertEquals(activityLog.getUserId(), userAchievement.get().getUserId());
        assertEquals(FirstStockPurchaseTwitter.ID, userAchievement.get().getAchievementId());
        assertEquals(activityLog.getTimestamp(), userAchievement.get().getTimestamp());
        assertEquals("First stock purchase completed - 5 shares of TWITTER/symbol bought for ⊻10 each",
                userAchievement.get().getDescription());
    }
}
