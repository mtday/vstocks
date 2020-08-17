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
    @Test
    public void testGettersAndSettersAll() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setPrice(20L);

        assertEquals("id", activityLog.getId());
        assertEquals(userId, activityLog.getUserId());
        assertEquals(STOCK_SELL, activityLog.getType());
        assertEquals(now, activityLog.getTimestamp());
        assertEquals(TWITTER, activityLog.getMarket());
        assertEquals("symbol", activityLog.getSymbol());
        assertEquals(10, (int) activityLog.getShares());
        assertEquals(20, (long) activityLog.getPrice());
    }

    @Test
    public void testGettersAndSettersSimple() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
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
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id")
                .setUserId(generateId("user@domain.com"))
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setPrice(20L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id")
                .setUserId(generateId("user@domain.com"))
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setPrice(20L);
        assertEquals(activityLog1, activityLog2);
    }

    @Test
    public void testHashCode() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setPrice(20L);
        assertEquals(-1807454463, new ActivityLog().hashCode());
        assertNotEquals(0, activityLog.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setPrice(20L);
        assertEquals("ActivityLog{id='id', userId='" + userId + "', type=STOCK_SELL, timestamp=" + now.toString()
                + ", market=TWITTER, symbol='symbol', shares=10, price=20}", activityLog.toString());
    }
}
