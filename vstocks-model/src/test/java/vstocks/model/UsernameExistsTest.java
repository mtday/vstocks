package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UsernameExistsTest {
    @Test
    public void testGettersAndSetters() {
        UsernameExists usernameExists = new UsernameExists()
                .setUsername("username")
                .setExists(true);

        assertEquals("username", usernameExists.getUsername());
        assertTrue(usernameExists.isExists());
    }

    @Test
    public void testEquals() {
        UsernameExists usernameExists1 = new UsernameExists()
                .setUsername("username")
                .setExists(true);
        UsernameExists usernameExists2 = new UsernameExists()
                .setUsername("username")
                .setExists(true);
        assertEquals(usernameExists1, usernameExists2);
    }

    @Test
    public void testHashCode() {
        UsernameExists usernameExists = new UsernameExists()
                .setUsername("username")
                .setExists(true);
        assertEquals(2198, new UsernameExists().hashCode());
        assertEquals(352819834, usernameExists.hashCode());
    }

    @Test
    public void testToString() {
        UsernameExists usernameExists = new UsernameExists()
                .setUsername("username")
                .setExists(true);
        assertEquals("UsernameExistsResponse{username='username', exists=true}", usernameExists.toString());
    }
}
