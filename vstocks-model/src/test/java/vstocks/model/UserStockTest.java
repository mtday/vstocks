package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class UserStockTest {
    @Test
    public void testGettersAndSetters() {
        UserStock userStock = new UserStock()
                .setUserId("TW:12345")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);

        assertEquals("TW:12345", userStock.getUserId());
        assertEquals(TWITTER, userStock.getMarket());
        assertEquals("symbol", userStock.getSymbol());
        assertEquals(10, userStock.getShares());
    }

    @Test
    public void testEquals() {
        UserStock userStock1 = new UserStock().setUserId("user").setMarket(TWITTER).setSymbol("sym").setShares(10);
        UserStock userStock2 = new UserStock().setUserId("user").setMarket(TWITTER).setSymbol("sym").setShares(20);
        assertEquals(userStock1, userStock2);
    }

    @Test
    public void testHashCode() {
        UserStock userStock = new UserStock()
                .setUserId("TW:12345")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);
        assertEquals(-381862016, userStock.hashCode());
    }

    @Test
    public void testToString() {
        UserStock userStock = new UserStock()
                .setUserId("TW:12345")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);
        assertEquals("UserStock{userId='TW:12345', market=TWITTER, symbol='symbol', shares=10}", userStock.toString());
    }
}
