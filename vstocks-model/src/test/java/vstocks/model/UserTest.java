package vstocks.model;

import org.junit.Test;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class UserTest {
    @Test
    public void testGettersAndSetters() {
        User user = new User()
                .setEmail("user@domain.com") // also sets id
                .setUsername("username")
                .setDisplayName("displayName")
                .setImageLink("link");

        assertEquals("cd2bfcff-e5fe-34a1-949d-101994d0987f", user.getId()); // automatically set via User#setEmail
        assertEquals("user@domain.com", user.getEmail());
        assertEquals("username", user.getName());
        assertEquals("username", user.getUsername());
        assertEquals("displayName", user.getDisplayName());
        assertEquals("link", user.getImageLink());
    }

    @Test
    public void testEquals() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        User user1 = new User().setId(userId).setUsername("username1");
        User user2 = new User().setId(userId).setUsername("username2");
        assertEquals(user1, user2);
    }

    @Test
    public void testHashCode() {
        User user = new User().setEmail("user@domain.com").setUsername("username");
        assertEquals(2060421285, user.hashCode());
    }

    @Test
    public void testToString() {
        User user = new User()
                .setEmail("user@domain.com") // also sets id
                .setUsername("username")
                .setDisplayName("displayName")
                .setImageLink("link");
        assertEquals("User{id='cd2bfcff-e5fe-34a1-949d-101994d0987f', email='user@domain.com', username='username', "
                + "displayName='displayName', imageLink='link'}", user.toString());
    }
}
