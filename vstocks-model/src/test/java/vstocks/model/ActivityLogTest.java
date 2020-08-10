package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static vstocks.model.ActivityType.STOCK_SELL;
import static vstocks.model.ActivityType.USER_LOGIN;
import static vstocks.model.Market.TWITTER;

public class ActivityLogTest {
    @Test
    public void testGettersAndSettersAll() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setPrice(20);

        assertEquals("id", activityLog.getId());
        assertEquals(userId, activityLog.getUserId());
        assertEquals(STOCK_SELL, activityLog.getType());
        assertEquals(now, activityLog.getTimestamp());
        assertEquals(TWITTER, activityLog.getMarket());
        assertEquals("symbol", activityLog.getSymbol());
        assertEquals(10, (int) activityLog.getShares());
        assertEquals(20, (int) activityLog.getPrice());
    }

    @Test
    public void testGettersAndSettersSimple() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
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
        ActivityLog activityLog1 = new ActivityLog().setId("id").setUserId("user1");
        ActivityLog activityLog2 = new ActivityLog().setId("id").setUserId("user2");
        assertEquals(activityLog1, activityLog2);
    }

    @Test
    public void testHashCode() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setPrice(20);
        assertEquals(3386, activityLog.hashCode());
    }

    @Test
    public void testToString() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(userId)
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setPrice(20);
        assertEquals("ActivityLog{id='id', userId='" + userId + "', type=STOCK_SELL, timestamp=" + now.toString()
                + ", market=TWITTER, symbol='symbol', shares=10, price=20}", activityLog.toString());
    }
}
