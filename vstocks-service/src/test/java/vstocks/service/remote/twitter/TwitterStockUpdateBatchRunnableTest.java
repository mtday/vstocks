package vstocks.service.remote.twitter;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.api.UsersResources;
import vstocks.model.Stock;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class TwitterStockUpdateBatchRunnableTest {
    private static User getUser(String username) {
        User user = mock(User.class);
        when(user.getScreenName()).thenReturn(username);
        return user;
    }

    @Test
    public void testNormalOperation() throws TwitterException {
        CustomResponseList<User> responseList = new CustomResponseList<>();
        responseList.addAll(asList(getUser("user1"), getUser("user2")));
        UsersResources usersResources = mock(UsersResources.class);
        when(usersResources.lookupUsers(ArgumentMatchers.<String>any())).thenReturn(responseList);
        Twitter twitter = mock(Twitter.class);
        when(twitter.users()).thenReturn(usersResources);

        List<Stock> stocks = Stream.of("user1", "user2")
                .map(symbol -> new Stock().setMarket(TWITTER).setSymbol(symbol).setName("Name"))
                .collect(toList());
        List<User> users = new ArrayList<>();
        new TwitterStockUpdateBatchRunnable(twitter, users::add, stocks).run();

        assertEquals(2, users.size());
    }

    @Test
    public void testUserNotFound() throws TwitterException {
        UsersResources usersResources = mock(UsersResources.class);
        when(usersResources.lookupUsers(ArgumentMatchers.<String>any()))
                .thenThrow(new TwitterException("User not found", new Exception(), 404));
        Twitter twitter = mock(Twitter.class);
        when(twitter.users()).thenReturn(usersResources);

        List<Stock> stocks = Stream.of("user1", "user2")
                .map(symbol -> new Stock().setMarket(TWITTER).setSymbol(symbol).setName("Name"))
                .collect(toList());
        List<User> users = new ArrayList<>();
        new TwitterStockUpdateBatchRunnable(twitter, users::add, stocks).run();

        assertTrue(users.isEmpty());
    }

    @Test
    public void testUnknownFailure() throws TwitterException {
        UsersResources usersResources = mock(UsersResources.class);
        when(usersResources.lookupUsers(ArgumentMatchers.<String>any()))
                .thenThrow(new TwitterException("User not found", new Exception(), 500));
        Twitter twitter = mock(Twitter.class);
        when(twitter.users()).thenReturn(usersResources);

        List<Stock> stocks = Stream.of("user1", "user2")
                .map(symbol -> new Stock().setMarket(TWITTER).setSymbol(symbol).setName("Name"))
                .collect(toList());
        List<User> users = new ArrayList<>();
        new TwitterStockUpdateBatchRunnable(twitter, users::add, stocks).run();

        assertTrue(users.isEmpty());
    }
}
