package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class ActivityLogTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId("userId")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20);

        assertEquals("id", activityLog.getId());
        assertEquals("userId", activityLog.getUserId());
        assertEquals(TWITTER, activityLog.getMarket());
        assertEquals("symbol", activityLog.getSymbol());
        assertEquals(now, activityLog.getTimestamp());
        assertEquals(10, activityLog.getShares());
        assertEquals(20, activityLog.getPrice());
    }

    @Test
    public void testEquals() {
        ActivityLog activityLog1 = new ActivityLog().setId("id").setUserId("user1");
        ActivityLog activityLog2 = new ActivityLog().setId("id").setUserId("user2");
        assertEquals(activityLog1, activityLog2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId("userId")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20);
        assertEquals(3386, activityLog.hashCode());
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId("userId")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20);
        assertEquals("ActivityLog{id='id', userId='userId', market=TWITTER, symbol='symbol', "
                + "timestamp=" + now.toString() + ", shares=10, price=20}", activityLog.toString());
    }
}
