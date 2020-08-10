package vstocks.model;

import org.junit.Test;

import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class UserStockTest {
    @Test
    public void testGettersAndSetters() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
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
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        UserStock userStock1 = new UserStock().setUserId(userId).setMarket(TWITTER).setSymbol("sym").setShares(10);
        UserStock userStock2 = new UserStock().setUserId(userId).setMarket(TWITTER).setSymbol("sym").setShares(20);
        assertEquals(userStock1, userStock2);
    }

    @Test
    public void testHashCode() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        UserStock userStock = new UserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);
        assertEquals(1638071562, userStock.hashCode());
    }

    @Test
    public void testToString() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        UserStock userStock = new UserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10);
        assertEquals("UserStock{userId='" + userId + "', market=TWITTER, symbol='symbol', shares=10}",
                userStock.toString());
    }
}
