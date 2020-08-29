package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.*;
import static vstocks.model.ActivityType.STOCK_SELL;
import static vstocks.model.ActivityType.USER_LOGIN;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.User.generateId;

public class ActivityLogTest {
    private final String userId = generateId("user@domain.com");
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSettersAll() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);

        assertEquals("id", activityLog.getId());
        assertEquals(userId, activityLog.getUserId());
        assertEquals(STOCK_SELL, activityLog.getType());
        assertEquals(now, activityLog.getTimestamp());
        assertEquals(TWITTER, activityLog.getMarket());
        assertEquals("symbol", activityLog.getSymbol());
        assertEquals(10, (long) activityLog.getShares());
        assertEquals(20, (long) activityLog.getPrice());
        assertEquals(200, (long) activityLog.getValue());
    }

    @Test
    public void testGettersAndSettersSimple() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(USER_LOGIN)
                .setTimestamp(now);

        assertEquals("id", activityLog.getId());
        assertEquals(userId, activityLog.getUserId());
        assertEquals(USER_LOGIN, activityLog.getType());
        assertEquals(now, activityLog.getTimestamp());
        assertNull(activityLog.getMarket());
        assertNull(activityLog.getSymbol());
        assertNull(activityLog.getShares());
        assertNull(activityLog.getPrice());
        assertNull(activityLog.getValue());
    }

    @Test
    public void testEquals() {
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id")
                .setUserId(generateId("user@domain.com"))
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id")
                .setUserId(generateId("user@domain.com"))
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);
        assertEquals(activityLog1, activityLog2);
    }

    @Test
    public void testHashCode() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(timestamp)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);
        assertEquals(2099534374, activityLog.hashCode());
    }

    @Test
    public void testToString() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);
        assertEquals("ActivityLog{id='id', userId='" + userId + "', type=STOCK_SELL, timestamp=" + now
                + ", market=Twitter, symbol='symbol', shares=10, price=20, value=200}", activityLog.toString());
    }
}
