package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserTest {
    @Test
    public void testGettersAndSetters() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");

        assertEquals("cd2bfcff-e5fe-34a1-949d-101994d0987f", user.getId()); // automatically set via User#setEmail
        assertEquals("user@domain.com", user.getEmail());
        assertEquals("username", user.getName());
        assertEquals("username", user.getUsername());
        assertEquals("displayName", user.getDisplayName());
    }

    @Test
    public void testEquals() {
        User user1 = new User().setId("id").setUsername("username1");
        User user2 = new User().setId("id").setUsername("username2");
        assertEquals(user1, user2);
    }

    @Test
    public void testHashCode() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        assertEquals(2060421285, user.hashCode());
    }

    @Test
    public void testToString() {
        User user = new User().setEmail("user@domain.com").setUsername("username").setDisplayName("displayName");
        assertEquals("User{id='cd2bfcff-e5fe-34a1-949d-101994d0987f', email='user@domain.com', username='username', "
                + "displayName='displayName'}", user.toString());
    }
}
