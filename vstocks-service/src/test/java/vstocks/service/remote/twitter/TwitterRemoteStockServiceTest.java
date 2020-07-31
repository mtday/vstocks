package vstocks.service.remote.twitter;

import org.junit.Test;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.api.UsersResources;
import twitter4j.conf.Configuration;
import vstocks.model.Stock;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;
import static vstocks.service.remote.twitter.TwitterRemoteStockService.getPrice;

public class TwitterRemoteStockServiceTest {
    //private static final Logger LOGGER = LoggerFactory.getLogger(TwitterRemoteStockServiceTest.class);

    @Test
    public void testCreateConfiguration() {
        Configuration configuration = TwitterRemoteStockService.createConfiguration();
        assertNotNull(configuration);
        assertNotNull(configuration.getOAuthConsumerKey());
        assertNotNull(configuration.getOAuthConsumerSecret());
        assertNotNull(configuration.getOAuthAccessToken());
        assertNotNull(configuration.getOAuthAccessTokenSecret());
    }

    private final Function<Integer, User> getUserWithFollowers = followers -> {
        User user = mock(User.class);
        when(user.getFollowersCount()).thenReturn(followers);
        return user;
    };

    @Test
    public void testGetPrice() {
        /*
        Stream.iterate(0, i -> i == 0 ? 5 : (("" + i).contains("5") ? i * 2 : i * 5)).limit(17).forEach(i ->
                LOGGER.info("Price: {} => {}", i, getPrice(getUserWithFollowers.apply(i))));
         */

        assertEquals(1, getPrice(getUserWithFollowers.apply(0)));
        assertEquals(1, getPrice(getUserWithFollowers.apply(5)));
        assertEquals(1, getPrice(getUserWithFollowers.apply(10)));
        assertEquals(1, getPrice(getUserWithFollowers.apply(50)));
        assertEquals(1, getPrice(getUserWithFollowers.apply(100)));
        assertEquals(3, getPrice(getUserWithFollowers.apply(500)));
        assertEquals(5, getPrice(getUserWithFollowers.apply(1_000)));
        assertEquals(25, getPrice(getUserWithFollowers.apply(5_000)));
        assertEquals(50, getPrice(getUserWithFollowers.apply(10_000)));
        assertEquals(239, getPrice(getUserWithFollowers.apply(50_000)));
        assertEquals(455, getPrice(getUserWithFollowers.apply(100_000)));
        assertEquals(1667, getPrice(getUserWithFollowers.apply(500_000)));
        assertEquals(2501, getPrice(getUserWithFollowers.apply(1_000_000)));
        assertEquals(4167, getPrice(getUserWithFollowers.apply(5_000_000)));
        assertEquals(4546, getPrice(getUserWithFollowers.apply(10_000_000)));
        assertEquals(4902, getPrice(getUserWithFollowers.apply(50_000_000)));
        assertEquals(4951, getPrice(getUserWithFollowers.apply(100_000_000)));
    }

    /* TODO
    @Test
    public void testUpdate() throws TwitterException {
        User user = mock(User.class);
        when(user.getName()).thenReturn("Name");
        when(user.getFollowersCount()).thenReturn(123456);
        UsersResources usersResources = mock(UsersResources.class);
        when(usersResources.showUser(eq("username"))).thenReturn(user);
        Twitter twitter = mock(Twitter.class);
        when(twitter.users()).thenReturn(usersResources);

        Stock stock = new Stock().setMarket(TWITTER).setSymbol("username").setName("User");
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol("username").setTimestamp(Instant.now()).setPrice(0);

        TwitterRemoteStockService twitterRemoteStockService = new TwitterRemoteStockService(twitter);
        twitterRemoteStockService.update(stock, stockPrice);

        assertEquals("Name", stock.getName()); // stock name updated
        assertEquals(getPrice(getUserWithFollowers.apply(123456)), stockPrice.getPrice()); // stock price updated
    }

    @Test
    public void testUpdateUserMissing() throws TwitterException {
        TwitterException exception = new TwitterException("message", new Exception(), 404);
        UsersResources usersResources = mock(UsersResources.class);
        when(usersResources.showUser(eq("username"))).thenThrow(exception);
        Twitter twitter = mock(Twitter.class);
        when(twitter.users()).thenReturn(usersResources);

        Stock stock = new Stock().setMarket(TWITTER).setSymbol("username").setName("User");
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol("username").setTimestamp(Instant.now()).setPrice(0);

        TwitterRemoteStockService twitterRemoteStockService = new TwitterRemoteStockService(twitter);
        twitterRemoteStockService.update(stock, stockPrice);

        assertEquals("User", stock.getName()); // stock name not updated
        assertEquals(1, stockPrice.getPrice()); // stock price set to 1
    }
     */

    @Test
    public void testSearch() throws TwitterException {
        User user1 = mock(User.class);
        when(user1.getScreenName()).thenReturn("user1");
        when(user1.getName()).thenReturn("User1");
        User user2 = mock(User.class);
        when(user2.getScreenName()).thenReturn("user2");
        when(user2.getName()).thenReturn("User2");

        @SuppressWarnings("unchecked")
        ResponseList<User> users = mock(ResponseList.class);
        when(users.stream()).thenReturn(Stream.of(user1, user2));

        UsersResources usersResources = mock(UsersResources.class);
        when(usersResources.searchUsers(eq("user"), anyInt())).thenReturn(users);
        Twitter twitter = mock(Twitter.class);
        when(twitter.users()).thenReturn(usersResources);

        TwitterRemoteStockService twitterRemoteStockService = new TwitterRemoteStockService(twitter);
        List<Stock> stocks = twitterRemoteStockService.search("user");

        assertEquals(2, stocks.size());
        assertEquals(TWITTER, stocks.get(0).getMarket());
        assertEquals("user1", stocks.get(0).getSymbol());
        assertEquals("User1", stocks.get(0).getName());
        assertEquals(TWITTER, stocks.get(1).getMarket());
        assertEquals("user2", stocks.get(1).getSymbol());
        assertEquals("User2", stocks.get(1).getName());
    }
}
