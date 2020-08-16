package vstocks.service.remote.twitter;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.api.UsersResources;
import twitter4j.conf.Configuration;
import vstocks.model.PricedStock;
import vstocks.model.Stock;
import vstocks.service.StockUpdateRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;
import static vstocks.service.remote.twitter.TwitterRemoteStockService.getPrice;
import static vstocks.service.remote.twitter.TwitterRemoteStockService.toPricedStock;

public class TwitterRemoteStockServiceTest {
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

    private static User getUser(String username, int followers, String name, String link) {
        User user = mock(User.class);
        when(user.getScreenName()).thenReturn(username);
        when(user.getFollowersCount()).thenReturn(followers);
        when(user.getName()).thenReturn(name);
        when(user.getProfileImageURLHttps()).thenReturn(link);
        return user;
    }

    @Test
    public void testToPricedStock() {
        PricedStock pricedStock = toPricedStock(getUser("user", 50_000, "name", "link"));

        assertEquals(TWITTER, pricedStock.getMarket());
        assertEquals("user", pricedStock.getSymbol());
        assertEquals("name", pricedStock.getName());
        assertEquals("link", pricedStock.getProfileImage());
        assertNotNull(pricedStock.getTimestamp());
        assertEquals(239, pricedStock.getPrice());
    }

    @Test
    public void testToPricedStockNullLink() {
        PricedStock pricedStock = toPricedStock(getUser("user", 50_000, "name", null));

        assertEquals(TWITTER, pricedStock.getMarket());
        assertEquals("user", pricedStock.getSymbol());
        assertEquals("name", pricedStock.getName());
        assertNull(pricedStock.getProfileImage());
        assertNotNull(pricedStock.getTimestamp());
        assertEquals(239, pricedStock.getPrice());
    }

    @Test
    public void testGetUpdateRunnable() throws TwitterException, ExecutionException, InterruptedException, IOException {
        User user1 = getUser("user1", 50_000, "User1", "link");
        User user2 = getUser("user2", 100_000, "User2", "link");
        CustomResponseList<User> responseList = new CustomResponseList<>();
        responseList.addAll(asList(user1, user2));
        UsersResources usersResources = mock(UsersResources.class);
        when(usersResources.lookupUsers(ArgumentMatchers.<String>any())).thenReturn(responseList);
        Twitter twitter = mock(Twitter.class);
        when(twitter.users()).thenReturn(usersResources);

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<PricedStock> entries = new ArrayList<>();

        TwitterRemoteStockService twitterRemoteStockService = new TwitterRemoteStockService(twitter);
        Future<?> future;
        try (StockUpdateRunnable runnable = twitterRemoteStockService.getUpdateRunnable(executorService, entries::add)) {
            runnable.accept(new Stock().setMarket(TWITTER).setSymbol("user1").setName("name"));
            runnable.accept(new Stock().setMarket(TWITTER).setSymbol("user2").setName("name"));
            future = executorService.submit(runnable);
        }
        future.get();

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException interrupted) {
            throw new RuntimeException(interrupted);
        }

        assertEquals(2, entries.size());

        assertEquals("user1", entries.get(0).getSymbol());
        assertEquals("User1", entries.get(0).getName());
        assertEquals("link", entries.get(0).getProfileImage());
        assertEquals(239, entries.get(0).getPrice());

        assertEquals("user2", entries.get(1).getSymbol());
        assertEquals("User2", entries.get(1).getName());
        assertEquals("link", entries.get(1).getProfileImage());
        assertEquals(455, entries.get(1).getPrice());
    }

    @Test
    public void testSearch() throws TwitterException {
        User user1 = getUser("user1", 50_000, "User1", "link1");
        User user2 = getUser("user2", 100_000, "User2", "link2");

        @SuppressWarnings("unchecked")
        ResponseList<User> users = mock(ResponseList.class);
        when(users.stream()).thenReturn(Stream.of(user1, user2));

        UsersResources usersResources = mock(UsersResources.class);
        when(usersResources.searchUsers(eq("user"), anyInt())).thenReturn(users);
        Twitter twitter = mock(Twitter.class);
        when(twitter.users()).thenReturn(usersResources);

        TwitterRemoteStockService twitterRemoteStockService = new TwitterRemoteStockService(twitter);
        List<PricedStock> stocks = twitterRemoteStockService.search("user", 20);

        assertEquals(2, stocks.size());
        assertEquals(TWITTER, stocks.get(0).getMarket());
        assertEquals("user1", stocks.get(0).getSymbol());
        assertEquals("User1", stocks.get(0).getName());
        assertEquals("link1", stocks.get(0).getProfileImage());
        assertEquals(239, stocks.get(0).getPrice());

        assertEquals(TWITTER, stocks.get(1).getMarket());
        assertEquals("user2", stocks.get(1).getSymbol());
        assertEquals("User2", stocks.get(1).getName());
        assertEquals("link2", stocks.get(1).getProfileImage());
        assertEquals(455, stocks.get(1).getPrice());
    }
}
