package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.*;
import static vstocks.model.User.generateId;

public class UserResetTest {
    private final User user = new User()
            .setId(generateId("user@domain.com"))
            .setEmail("user@domain.com")
            .setUsername("user")
            .setDisplayName("User")
            .setProfileImage("link");

    @Test
    public void testGettersAndSetters() {
        UserReset userReset = new UserReset().setUser(user).setReset(true);

        assertEquals(user, userReset.getUser());
        assertTrue(userReset.isReset());
    }

    @Test
    public void testEquals() {
        UserReset userReset1 = new UserReset().setUser(user).setReset(true);
        UserReset userReset2 = new UserReset().setUser(user).setReset(true);
        assertEquals(userReset1, userReset2);
    }

    @Test
    public void testHashCode() {
        UserReset userReset = new UserReset().setUser(user).setReset(true);
        assertEquals(-628430147, userReset.hashCode());
    }

    @Test
    public void testToString() {
        UserReset userReset = new UserReset().setUser(user).setReset(true);
        assertEquals("UserReset{user=" + user + ", reset=true}", userReset.toString());
    }
}
