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
}
