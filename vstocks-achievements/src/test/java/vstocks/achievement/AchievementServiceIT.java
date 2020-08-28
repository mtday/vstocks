package vstocks.achievement;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.ServiceFactoryImpl;
import vstocks.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.User.generateId;

public class AchievementServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private ServiceFactory serviceFactory = new ServiceFactoryImpl(dataSourceExternalResource.get());

    @Before
    public void setup() {
        serviceFactory = new ServiceFactoryImpl(dataSourceExternalResource.get());
    }

    @After
    public void cleanup() {
        serviceFactory.getActivityLogService().truncate();
        serviceFactory.getStockService().truncate();
        serviceFactory.getUserService().truncate();
    }

    @Test
    public void testGetAchievements() {
        AchievementService achievementService = new AchievementService();
        String achievements = achievementService.getAchievements().stream()
                .map(achievement -> achievement.getCategory().getDisplayName() + " - " + achievement.getName())
                .collect(joining("\n"));

        String expected = String.join("\n", asList(
                "Beginner - First Facebook Stock Purchase",
                "Beginner - First Instagram Stock Purchase",
                "Beginner - First Twitch Stock Purchase",
                "Beginner - First Twitter Stock Purchase",
                "Beginner - First YouTube Stock Purchase",
                "Beginner - First Facebook Stock Sale",
                "Beginner - First Instagram Stock Sale",
                "Beginner - First Twitch Stock Sale",
                "Beginner - First Twitter Stock Sale",
                "Beginner - First YouTube Stock Sale"
        ));
        assertEquals(expected, achievements);
    }

    @Test
    public void testCheck() {
        ServiceFactory serviceFactory = new ServiceFactoryImpl(dataSourceExternalResource.get());

        Instant now = Instant.now().truncatedTo(SECONDS);
        User user = new User().setId(generateId("testuser@domain.com")).setEmail("testuser@domain.com").setUsername("testuser").setDisplayName("User");
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setProfileImage("link");
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol("symbol").setPrice(10).setTimestamp(now);

        ActivityLog activityLog = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(STOCK_BUY)
                .setTimestamp(now)
                .setMarket(stock.getMarket())
                .setSymbol(stock.getSymbol())
                .setShares(5L)
                .setPrice(stockPrice.getPrice())
                .setValue(5L * stockPrice.getPrice());

        assertEquals(1, serviceFactory.getUserService().add(user));
        assertEquals(1, serviceFactory.getStockService().add(stock));
        assertEquals(1, serviceFactory.getStockPriceService().add(stockPrice));
        assertEquals(1, serviceFactory.getActivityLogService().add(activityLog));

        AchievementService achievementService = new AchievementService();
        List<UserAchievement> achievements = new ArrayList<>();
        assertEquals(1, achievementService.find(serviceFactory, singleton(user.getId()), achievements::add));
        assertEquals(1, achievements.size());

        UserAchievement userAchievement = achievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_buy_twitter", userAchievement.getAchievementId());
        assertEquals(activityLog.getTimestamp(), userAchievement.getTimestamp());
        assertEquals("First Twitter stock purchase achieved - 5 shares of symbol bought for 10 credits each.",
                userAchievement.getDescription());
    }
}
