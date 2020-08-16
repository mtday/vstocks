package vstocks.model.rest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UsernameExistsResponseTest {
    @Test
    public void testGettersAndSetters() {
        UsernameExistsResponse usernameExistsResponse = new UsernameExistsResponse()
                .setUsername("username")
                .setExists(true);

        assertEquals("username", usernameExistsResponse.getUsername());
        assertTrue(usernameExistsResponse.isExists());
    }

    @Test
    public void testEquals() {
        UsernameExistsResponse usernameExistsResponse1 = new UsernameExistsResponse()
                .setUsername("username")
                .setExists(true);
        UsernameExistsResponse usernameExistsResponse2 = new UsernameExistsResponse()
                .setUsername("username")
                .setExists(true);
        assertEquals(usernameExistsResponse1, usernameExistsResponse2);
    }

    @Test
    public void testHashCode() {
        UsernameExistsResponse usernameExistsResponse = new UsernameExistsResponse()
                .setUsername("username")
                .setExists(true);
        assertEquals(2198, new UsernameExistsResponse().hashCode());
        assertEquals(352819834, usernameExistsResponse.hashCode());
    }

    @Test
    public void testToString() {
        UsernameExistsResponse usernameExistsResponse = new UsernameExistsResponse()
                .setUsername("username")
                .setExists(true);
        assertEquals("UsernameExistsResponse{username='username', exists=true}", usernameExistsResponse.toString());
    }
}
