package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.User.generateId;

public class UserStockTest {
    private final String userId = generateId("user@domain.com");

    @Test
    public void testGettersAndSetters() {
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
        UserStock userStock = new UserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);
        assertEquals(-759389120, userStock.hashCode());
    }

    @Test
    public void testToString() {
        UserStock userStock = new UserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);
        assertEquals("UserStock{userId='" + userId + "', market=Twitter, symbol='symbol', shares=10}",
                userStock.toString());
    }
}
