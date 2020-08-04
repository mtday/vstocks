package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserBalanceTest {
    @Test
    public void testGettersAndSetters() {
        UserBalance userBalance = new UserBalance().setUserId("TW:12345").setBalance(10);

        assertEquals("TW:12345", userBalance.getUserId());
        assertEquals(10, userBalance.getBalance());
    }

    @Test
    public void testEquals() {
        UserBalance userBalance1 = new UserBalance().setUserId("TW:12345").setBalance(10);
        UserBalance userBalance2 = new UserBalance().setUserId("TW:12345").setBalance(20);
        assertEquals(userBalance1, userBalance2);
    }

    @Test
    public void testHashCode() {
        UserBalance userBalance = new UserBalance().setUserId("TW:12345").setBalance(10);
        assertEquals(1977872539, userBalance.hashCode());
    }

    @Test
    public void testToString() {
        UserBalance userBalance = new UserBalance().setUserId("TW:12345").setBalance(10);
        assertEquals("UserBalance{userId='TW:12345', balance=10}", userBalance.toString());
    }
}
