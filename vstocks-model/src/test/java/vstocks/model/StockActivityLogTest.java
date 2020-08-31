package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static vstocks.model.ActivityType.STOCK_SELL;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.User.generateId;

public class StockActivityLogTest {
    private final String userId = generateId("user@domain.com");
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSettersAll() {
        StockActivityLog stockActivityLog = new StockActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("image")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);

        assertEquals("id", stockActivityLog.getId());
        assertEquals(userId, stockActivityLog.getUserId());
        assertEquals(STOCK_SELL, stockActivityLog.getType());
        assertEquals(now, stockActivityLog.getTimestamp());
        assertEquals(TWITTER, stockActivityLog.getMarket());
        assertEquals("symbol", stockActivityLog.getSymbol());
        assertEquals("name", stockActivityLog.getName());
        assertEquals("image", stockActivityLog.getProfileImage());
        assertEquals(10, (long) stockActivityLog.getShares());
        assertEquals(20, (long) stockActivityLog.getPrice());
        assertEquals(200, (long) stockActivityLog.getValue());
    }

    @Test
    public void testEquals() {
        StockActivityLog stockActivityLog1 = new StockActivityLog()
                .setId("id")
                .setUserId(generateId("user@domain.com"))
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("image")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);
        StockActivityLog stockActivityLog2 = new StockActivityLog()
                .setId("id")
                .setUserId(generateId("user@domain.com"))
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("image")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);
        assertEquals(stockActivityLog1, stockActivityLog2);
    }

    @Test
    public void testHashCode() {
        StockActivityLog stockActivityLog = new StockActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(timestamp)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("image")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);
        assertEquals(1247904868, stockActivityLog.hashCode());
    }

    @Test
    public void testToString() {
        StockActivityLog stockActivityLog = new StockActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("image")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);
        assertEquals("StockActivityLog{id='id', userId='" + userId + "', type=STOCK_SELL, timestamp=" + now
                + ", market=Twitter, symbol='symbol', name='name', profileImage='image', shares=10, price=20, "
                + "value=200}", stockActivityLog.toString());
    }
}
