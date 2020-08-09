package vstocks.achievement;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DBFactory;
import vstocks.db.jdbc.JdbcDBFactory;
import vstocks.db.jdbc.table.ActivityLogTable;
import vstocks.db.jdbc.table.StockTable;
import vstocks.db.jdbc.table.UserTable;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.Market.TWITTER;

public class AchievementServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            new ActivityLogTable().truncate(connection);
            new StockTable().truncate(connection);
            new UserTable().truncate(connection);
            connection.commit();
        }
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
        DBFactory dbFactory = new JdbcDBFactory(dataSourceExternalResource.get());

        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        User user = new User().setEmail("testuser@domain.com").setUsername("testuser").setDisplayName("User");
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setActive(true);
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol("symbol").setPrice(10).setTimestamp(now);

        ActivityLog activityLog = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(STOCK_BUY)
                .setTimestamp(now)
                .setMarket(stock.getMarket())
                .setSymbol(stock.getSymbol())
                .setShares(5)
                .setPrice(stockPrice.getPrice());

        assertEquals(1, dbFactory.getUserDB().add(user));
        assertEquals(1, dbFactory.getStockDB().add(stock));
        assertEquals(1, dbFactory.getStockPriceDB().add(stockPrice));
        assertEquals(1, dbFactory.getActivityLogDB().add(activityLog));

        AchievementService achievementService = new AchievementService();
        List<UserAchievement> achievements = new ArrayList<>();
        assertEquals(1, achievementService.find(dbFactory, singleton(user.getId()), achievements::add));
        assertEquals(1, achievements.size());

        UserAchievement userAchievement = achievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_buy_twitter", userAchievement.getAchievementId());
        assertEquals(activityLog.getTimestamp(), userAchievement.getTimestamp());
        assertEquals("First Twitter stock purchase achieved - 5 shares of symbol bought for 10 credits each.",
                userAchievement.getDescription());
    }
}
