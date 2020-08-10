package vstocks.model;

import org.junit.Test;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class UserCreditsTest {
    @Test
    public void testGettersAndSetters() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        UserCredits userCredits = new UserCredits().setUserId(userId).setCredits(10);

        assertEquals(userId, userCredits.getUserId());
        assertEquals(10, userCredits.getCredits());
    }

    @Test
    public void testEquals() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        UserCredits userCredits1 = new UserCredits().setUserId(userId).setCredits(10);
        UserCredits userCredits2 = new UserCredits().setUserId(userId).setCredits(20);
        assertEquals(userCredits1, userCredits2);
    }

    @Test
    public void testHashCode() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        UserCredits userCredits = new UserCredits().setUserId(userId).setCredits(10);
        assertEquals(2060421285, userCredits.hashCode());
    }

    @Test
    public void testToString() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        UserCredits userCredits = new UserCredits().setUserId(userId).setCredits(10);
        assertEquals("UserCredits{userId='" + userId + "', credits=10}", userCredits.toString());
    }
}
