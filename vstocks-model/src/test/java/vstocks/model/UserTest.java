package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static vstocks.model.UserSource.TwitterClient;

public class UserTest {
    @Test
    public void testGettersAndSetters() {
        User user = new User()
                .setId("TW:12345")
                .setUsername("username")
                .setSource(TwitterClient)
                .setDisplayName("displayName");

        assertEquals("TW:12345", user.getId());
        assertEquals("username", user.getName());
        assertEquals("username", user.getUsername());
        assertEquals(TwitterClient, user.getSource());
        assertEquals("displayName", user.getDisplayName());
    }

    @Test
    public void testEquals() {
        User user1 = new User().setId("TW:12345").setUsername("username1");
        User user2 = new User().setId("TW:12345").setUsername("username2");
        assertEquals(user1, user2);
    }

    @Test
    public void testHashCode() {
        User user = new User()
                .setId("TW:12345")
                .setUsername("username")
                .setSource(TwitterClient)
                .setDisplayName("displayName");
        assertEquals(1977872539, user.hashCode());
    }

    @Test
    public void testToString() {
        User user = new User()
                .setId("TW:12345")
                .setUsername("username")
                .setSource(TwitterClient)
                .setDisplayName("displayName");
        assertEquals("User{id='TW:12345', username='username', source=TwitterClient, displayName='displayName'}", user.toString());
    }
}
