package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;

public class UserStockTest {
    @Test
    public void testGettersAndSetters() {
        String userId = User.generateId("user@domain.com");
        UserStock userStock = new UserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);

        assertEquals(userId, userStock.getUserId());
        assertEquals(TWITTER, userStock.getMarket());
        assertEquals("symbol", userStock.getSymbol());
        assertEquals(10, userStock.getShares());
    }

    @Test
    public void testEquals() {
        String userId = User.generateId("user@domain.com");
        UserStock userStock1 = new UserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);
        UserStock userStock2 = new UserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);
        assertEquals(userStock1, userStock2);
    }

    @Test
    public void testHashCode() {
        String userId = User.generateId("user@domain.com");
        UserStock userStock = new UserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);
        assertEquals(923521, new UserStock().hashCode());
        assertNotEquals(0, userStock.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        String userId = User.generateId("user@domain.com");
        UserStock userStock = new UserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);
        assertEquals("UserStock{userId='" + userId + "', market=Twitter, symbol='symbol', shares=10}",
                userStock.toString());
    }
}
