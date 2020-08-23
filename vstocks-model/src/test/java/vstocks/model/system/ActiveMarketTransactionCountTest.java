package vstocks.model.system;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;

public class ActiveMarketTransactionCountTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        ActiveMarketTransactionCount activeMarketTransactionCount = new ActiveMarketTransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setCount(20);

        assertEquals(TWITTER, activeMarketTransactionCount.getMarket());
        assertEquals(now, activeMarketTransactionCount.getTimestamp());
        assertEquals(20, activeMarketTransactionCount.getCount());
    }

    @Test
    public void testEquals() {
        ActiveMarketTransactionCount activeMarketTransactionCount1 = new ActiveMarketTransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setCount(20);
        ActiveMarketTransactionCount activeMarketTransactionCount2 = new ActiveMarketTransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setCount(20);
        assertEquals(activeMarketTransactionCount1, activeMarketTransactionCount2);
    }

    @Test
    public void testHashCode() {
        ActiveMarketTransactionCount activeMarketTransactionCount = new ActiveMarketTransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(timestamp)
                .setCount(20);
        assertEquals(29791, new ActiveMarketTransactionCount().hashCode());
        assertNotEquals(0, activeMarketTransactionCount.hashCode()); // enums makes the value inconsistent
    }

    @Test
    public void testToString() {
        ActiveMarketTransactionCount activeMarketTransactionCount = new ActiveMarketTransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setCount(20);
        assertEquals("ActiveMarketTransactionCount{market=Twitter, timestamp=" + now + ", count=20}",
                activeMarketTransactionCount.toString());
    }
}
