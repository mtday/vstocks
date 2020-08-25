package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UsernameCheckTest {
    @Test
    public void testGettersAndSetters() {
        UsernameCheck usernameCheck = new UsernameCheck()
                .setUsername("username")
                .setExists(true)
                .setValid(true)
                .setMessage("message");

        assertEquals("username", usernameCheck.getUsername());
        assertTrue(usernameCheck.isExists());
        assertTrue(usernameCheck.isValid());
        assertEquals("message", usernameCheck.getMessage());
    }

    @Test
    public void testEquals() {
        UsernameCheck usernameCheck1 = new UsernameCheck()
                .setUsername("username")
                .setExists(true)
                .setValid(true)
                .setMessage("message");
        UsernameCheck usernameCheck2 = new UsernameCheck()
                .setUsername("username")
                .setExists(true)
                .setValid(true)
                .setMessage("message");
        assertEquals(usernameCheck1, usernameCheck2);
    }

    @Test
    public void testHashCode() {
        UsernameCheck usernameCheck = new UsernameCheck()
                .setUsername("username")
                .setExists(true)
                .setValid(true)
                .setMessage("message");
        assertEquals(2150625, new UsernameCheck().hashCode());
        assertEquals(712407314, usernameCheck.hashCode());
    }

    @Test
    public void testToString() {
        UsernameCheck usernameCheck = new UsernameCheck()
                .setUsername("username")
                .setExists(true)
                .setValid(true)
                .setMessage("message");
        assertEquals("UsernameCheck{username='username', exists=true, valid=true, message='message'}",
                usernameCheck.toString());
    }
}
