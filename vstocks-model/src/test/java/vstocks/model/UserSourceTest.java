package vstocks.model;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.UserSource.TwitterClient;

public class UserSourceTest {
    @Test
    public void testAbbreviation() {
        assertEquals("TW", TwitterClient.getAbbreviation());
    }

    @Test
    public void testFromExactMatch() {
        Optional<UserSource> userSource = UserSource.fromClientName("TwitterClient");
        assertTrue(userSource.isPresent());
        assertEquals(TwitterClient, userSource.get());
    }

    @Test
    public void testFromWrongCase() {
        Optional<UserSource> userSource = UserSource.fromClientName("twitterclient");
        assertTrue(userSource.isPresent());
        assertEquals(TwitterClient, userSource.get());
    }

    @Test
    public void testFromAbbreviationExactMatch() {
        Optional<UserSource> userSource = UserSource.fromClientName("TW");
        assertTrue(userSource.isPresent());
        assertEquals(TwitterClient, userSource.get());
    }

    @Test
    public void testFromAbbreviationWrongCase() {
        Optional<UserSource> userSource = UserSource.fromClientName("tw");
        assertTrue(userSource.isPresent());
        assertEquals(TwitterClient, userSource.get());
    }
}
