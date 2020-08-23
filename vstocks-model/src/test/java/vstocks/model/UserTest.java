package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static vstocks.model.User.generateId;

public class UserTest {
    @Test
    public void testGettersAndSetters() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("username")
                .setDisplayName("displayName")
                .setProfileImage("link");

        assertEquals("cd2bfcff-e5fe-34a1-949d-101994d0987f", user.getId());
        assertEquals("user@domain.com", user.getEmail());
        assertEquals("username", user.getName());
        assertEquals("username", user.getUsername());
        assertEquals("displayName", user.getDisplayName());
        assertEquals("link", user.getProfileImage());
    }

    @Test
    public void testGenerateId() {
        assertEquals("cd2bfcff-e5fe-34a1-949d-101994d0987f", generateId("user@domain.com"));
        assertEquals("cd2bfcff-e5fe-34a1-949d-101994d0987f", generateId("USER@DOMAIN.COM"));
    }

    @Test
    public void testEquals() {
        User user1 = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("username")
                .setDisplayName("displayName")
                .setProfileImage("link");
        User user2 = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("username")
                .setDisplayName("displayName")
                .setProfileImage("link");
        assertEquals(user1, user2);
    }

    @Test
    public void testHashCode() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("username")
                .setDisplayName("displayName")
                .setProfileImage("link");
        assertEquals(28629151, new User().hashCode());
        assertEquals(-1831566276, user.hashCode());
    }

    @Test
    public void testToString() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("username")
                .setDisplayName("displayName")
                .setProfileImage("link");
        assertEquals("User{id='cd2bfcff-e5fe-34a1-949d-101994d0987f', email='user@domain.com', username='username', "
                + "displayName='displayName', profileImage='link'}", user.toString());
    }
}
