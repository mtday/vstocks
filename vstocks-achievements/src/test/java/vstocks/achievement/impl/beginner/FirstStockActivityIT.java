package vstocks.achievement.impl.beginner;

import org.junit.Test;
import vstocks.achievement.AchievementFinder;
import vstocks.achievement.impl.BaseAchievementProviderIT;
import vstocks.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.*;
import static vstocks.model.AchievementCategory.BEGINNER;
import static vstocks.model.ActivityType.*;

public class FirstStockActivityIT extends BaseAchievementProviderIT {
    @Test
    public void testAchievementCount() {
        List<Entry<Achievement, AchievementFinder>> achievements = new FirstStockActivity().getAchievements();
        assertEquals(2 * Market.values().length, achievements.size()); // a buy and sell for each market
    }

    @Test
    public void testAchievementIds() {
        List<Entry<Achievement, AchievementFinder>> achievements = new FirstStockActivity().getAchievements();

        String expectedIds = String.join("\n", asList(
                "first_stock_buy_twitter",
                "first_stock_sell_twitter",
                "first_stock_buy_youtube",
                "first_stock_sell_youtube",
                "first_stock_buy_instagram",
                "first_stock_sell_instagram",
                "first_stock_buy_twitch",
                "first_stock_sell_twitch",
                "first_stock_buy_facebook",
                "first_stock_sell_facebook"
        ));
        String ids = achievements.stream().map(Entry::getKey).map(Achievement::getId).collect(joining("\n"));
        assertEquals(expectedIds, ids);
    }

    @Test
    public void testAchievementNames() {
        List<Entry<Achievement, AchievementFinder>> achievements = new FirstStockActivity().getAchievements();

        String expectedNames = String.join("\n", asList(
                "First Twitter Stock Purchase",
                "First Twitter Stock Sale",
                "First YouTube Stock Purchase",
                "First YouTube Stock Sale",
                "First Instagram Stock Purchase",
                "First Instagram Stock Sale",
                "First Twitch Stock Purchase",
                "First Twitch Stock Sale",
                "First Facebook Stock Purchase",
                "First Facebook Stock Sale"
        ));
        String names = achievements.stream().map(Entry::getKey).map(Achievement::getName).collect(joining("\n"));
        assertEquals(expectedNames, names);
    }

    @Test
    public void testAchievementCategory() {
        List<Entry<Achievement, AchievementFinder>> achievements = new FirstStockActivity().getAchievements();
        assertTrue(achievements.stream().map(Entry::getKey).allMatch(a -> a.getCategory() == BEGINNER));
    }

    @Test
    public void testAchievementOrder() {
        List<Entry<Achievement, AchievementFinder>> achievements = new FirstStockActivity().getAchievements();
        // buying orders before selling
        assertTrue(achievements.stream().map(Entry::getKey).filter(a -> a.getId().contains("stock_buy")).allMatch(a -> a.getOrder() == 5));
        assertTrue(achievements.stream().map(Entry::getKey).filter(a -> a.getId().contains("stock_sell")).allMatch(a -> a.getOrder() == 10));
    }

    @Test
    public void testAchievementDescriptions() {
        List<Entry<Achievement, AchievementFinder>> achievements = new FirstStockActivity().getAchievements();

        String expectedDescriptions = String.join("\n", asList(
                "Buy (with credits) one or more shares of any stock on the Twitter market for any price.",
                "Sell one or more shares of any stock on the Twitter market for any price.",
                "Buy (with credits) one or more shares of any stock on the YouTube market for any price.",
                "Sell one or more shares of any stock on the YouTube market for any price.",
                "Buy (with credits) one or more shares of any stock on the Instagram market for any price.",
                "Sell one or more shares of any stock on the Instagram market for any price.",
                "Buy (with credits) one or more shares of any stock on the Twitch market for any price.",
                "Sell one or more shares of any stock on the Twitch market for any price.",
                "Buy (with credits) one or more shares of any stock on the Facebook market for any price.",
                "Sell one or more shares of any stock on the Facebook market for any price."
        ));
        String descriptions = achievements.stream().map(Entry::getKey).map(Achievement::getDescription).collect(joining("\n"));
        assertEquals(expectedDescriptions, descriptions);
    }

    @Test
    public void testValidateNoActivity() {
        Stream<AchievementFinder> finders = new FirstStockActivity().getAchievements().stream().map(Entry::getValue);

        // with no activity in the db, none of the finders will match
        List<UserAchievement> userAchievements = new ArrayList<>();
        assertEquals(0, finders.mapToInt(f -> f.find(getDBFactory(), singleton(user.getId()), userAchievements::add)).sum());
        assertTrue(userAchievements.isEmpty());
    }

    @Test
    public void testValidateNoBuySellActivity() {
        Stream<AchievementFinder> finders = new FirstStockActivity().getAchievements().stream().map(Entry::getValue);

        ActivityLog activityLog = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(USER_LOGIN)
                .setTimestamp(Instant.now().truncatedTo(SECONDS));

        getDBFactory().getActivityLogService().add(activityLog);

        // with no buy/sell activity in the db, none of the finders will match
        List<UserAchievement> userAchievements = new ArrayList<>();
        assertEquals(0, finders.mapToInt(f -> f.find(getDBFactory(), singleton(user.getId()), userAchievements::add)).sum());
        assertTrue(userAchievements.isEmpty());
    }

    private List<UserAchievement> getUserAchievements(ActivityType activityType, Stock stock) {
        Stream<AchievementFinder> finders = new FirstStockActivity().getAchievements().stream().map(Entry::getValue);

        ActivityLog activityLog = new ActivityLog()
                .setId(UUID.randomUUID().toString())
                .setUserId(user.getId())
                .setType(activityType)
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setMarket(stock.getMarket())
                .setSymbol(stock.getSymbol())
                .setShares(activityType == STOCK_BUY ? 5L : -5L) // negative when selling
                .setPrice(10L)
                .setValue(10L * (activityType == STOCK_BUY ? 5L : -5L));

        getDBFactory().getActivityLogService().add(activityLog);

        List<UserAchievement> userAchievements = new ArrayList<>();
        finders.forEach(f -> f.find(getDBFactory(), singleton(user.getId()), userAchievements::add));
        return userAchievements;
    }

    @Test
    public void testValidateTwitterBuy() {
        List<UserAchievement> userAchievements = getUserAchievements(STOCK_BUY, twitterStock);
        assertEquals(1, userAchievements.size());
        UserAchievement userAchievement = userAchievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_buy_twitter", userAchievement.getAchievementId());
        assertNotNull(userAchievement.getTimestamp());
        assertEquals("First Twitter stock purchase achieved - 5 shares of symbol bought for 10 credits each.",
                userAchievement.getDescription());
    }

    @Test
    public void testValidateYouTubeBuy() {
        List<UserAchievement> userAchievements = getUserAchievements(STOCK_BUY, youtubeStock);
        assertEquals(1, userAchievements.size());
        UserAchievement userAchievement = userAchievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_buy_youtube", userAchievement.getAchievementId());
        assertNotNull(userAchievement.getTimestamp());
        assertEquals("First YouTube stock purchase achieved - 5 shares of symbol bought for 10 credits each.",
                userAchievement.getDescription());
    }

    @Test
    public void testValidateInstagramBuy() {
        List<UserAchievement> userAchievements = getUserAchievements(STOCK_BUY, instagramStock);
        assertEquals(1, userAchievements.size());
        UserAchievement userAchievement = userAchievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_buy_instagram", userAchievement.getAchievementId());
        assertNotNull(userAchievement.getTimestamp());
        assertEquals("First Instagram stock purchase achieved - 5 shares of symbol bought for 10 credits each.",
                userAchievement.getDescription());
    }

    @Test
    public void testValidateTwitchBuy() {
        List<UserAchievement> userAchievements = getUserAchievements(STOCK_BUY, twitchStock);
        assertEquals(1, userAchievements.size());
        UserAchievement userAchievement = userAchievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_buy_twitch", userAchievement.getAchievementId());
        assertNotNull(userAchievement.getTimestamp());
        assertEquals("First Twitch stock purchase achieved - 5 shares of symbol bought for 10 credits each.",
                userAchievement.getDescription());
    }

    @Test
    public void testValidateFacebookBuy() {
        List<UserAchievement> userAchievements = getUserAchievements(STOCK_BUY, facebookStock);
        assertEquals(1, userAchievements.size());
        UserAchievement userAchievement = userAchievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_buy_facebook", userAchievement.getAchievementId());
        assertNotNull(userAchievement.getTimestamp());
        assertEquals("First Facebook stock purchase achieved - 5 shares of symbol bought for 10 credits each.",
                userAchievement.getDescription());
    }

    @Test
    public void testValidateTwitterSell() {
        List<UserAchievement> userAchievements = getUserAchievements(STOCK_SELL, twitterStock);
        assertEquals(1, userAchievements.size());
        UserAchievement userAchievement = userAchievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_sell_twitter", userAchievement.getAchievementId());
        assertNotNull(userAchievement.getTimestamp());
        assertEquals("First Twitter stock sale achieved - 5 shares of symbol sold for 10 credits each.",
                userAchievement.getDescription());
    }

    @Test
    public void testValidateYouTubeSell() {
        List<UserAchievement> userAchievements = getUserAchievements(STOCK_SELL, youtubeStock);
        assertEquals(1, userAchievements.size());
        UserAchievement userAchievement = userAchievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_sell_youtube", userAchievement.getAchievementId());
        assertNotNull(userAchievement.getTimestamp());
        assertEquals("First YouTube stock sale achieved - 5 shares of symbol sold for 10 credits each.",
                userAchievement.getDescription());
    }

    @Test
    public void testValidateInstagramSell() {
        List<UserAchievement> userAchievements = getUserAchievements(STOCK_SELL, instagramStock);
        assertEquals(1, userAchievements.size());
        UserAchievement userAchievement = userAchievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_sell_instagram", userAchievement.getAchievementId());
        assertNotNull(userAchievement.getTimestamp());
        assertEquals("First Instagram stock sale achieved - 5 shares of symbol sold for 10 credits each.",
                userAchievement.getDescription());
    }

    @Test
    public void testValidateTwitchSell() {
        List<UserAchievement> userAchievements = getUserAchievements(STOCK_SELL, twitchStock);
        assertEquals(1, userAchievements.size());
        UserAchievement userAchievement = userAchievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_sell_twitch", userAchievement.getAchievementId());
        assertNotNull(userAchievement.getTimestamp());
        assertEquals("First Twitch stock sale achieved - 5 shares of symbol sold for 10 credits each.",
                userAchievement.getDescription());
    }

    @Test
    public void testValidateFacebookSell() {
        List<UserAchievement> userAchievements = getUserAchievements(STOCK_SELL, facebookStock);
        assertEquals(1, userAchievements.size());
        UserAchievement userAchievement = userAchievements.iterator().next();
        assertEquals(user.getId(), userAchievement.getUserId());
        assertEquals("first_stock_sell_facebook", userAchievement.getAchievementId());
        assertNotNull(userAchievement.getTimestamp());
        assertEquals("First Facebook stock sale achieved - 5 shares of symbol sold for 10 credits each.",
                userAchievement.getDescription());
    }
}
