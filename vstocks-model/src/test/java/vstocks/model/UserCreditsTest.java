package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserCreditsTest {
    @Test
    public void testGettersAndSetters() {
        UserCredits userCredits = new UserCredits().setUserId("TW:12345").setCredits(10);

        assertEquals("TW:12345", userCredits.getUserId());
        assertEquals(10, userCredits.getCredits());
    }

    @Test
    public void testEquals() {
        UserCredits userCredits1 = new UserCredits().setUserId("TW:12345").setCredits(10);
        UserCredits userCredits2 = new UserCredits().setUserId("TW:12345").setCredits(20);
        assertEquals(userCredits1, userCredits2);
    }

    @Test
    public void testHashCode() {
        UserCredits userCredits = new UserCredits().setUserId("TW:12345").setCredits(10);
        assertEquals(1977872539, userCredits.hashCode());
    }

    @Test
    public void testToString() {
        UserCredits userCredits = new UserCredits().setUserId("TW:12345").setCredits(10);
        assertEquals("UserCredits{userId='TW:12345', credits=10}", userCredits.toString());
    }
}
