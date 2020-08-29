package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserCreditsTest {
    @Test
    public void testGettersAndSetters() {
        String userId = User.generateId("user@domain.com");
        UserCredits userCredits = new UserCredits().setUserId(userId).setCredits(10);

        assertEquals(userId, userCredits.getUserId());
        assertEquals(10, userCredits.getCredits());
    }

    @Test
    public void testEquals() {
        String userId = User.generateId("user@domain.com");
        UserCredits userCredits1 = new UserCredits().setUserId(userId).setCredits(10);
        UserCredits userCredits2 = new UserCredits().setUserId(userId).setCredits(10);
        assertEquals(userCredits1, userCredits2);
    }

    @Test
    public void testHashCode() {
        String userId = User.generateId("user@domain.com");
        UserCredits userCredits = new UserCredits().setUserId(userId).setCredits(10);
        assertEquals(-551449595, userCredits.hashCode());
    }

    @Test
    public void testToString() {
        String userId = User.generateId("user@domain.com");
        UserCredits userCredits = new UserCredits().setUserId(userId).setCredits(10);
        assertEquals("UserCredits{userId='" + userId + "', credits=10}", userCredits.toString());
    }
}
