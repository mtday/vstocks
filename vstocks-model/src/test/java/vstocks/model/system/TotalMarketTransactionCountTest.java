package vstocks.model.system;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;

public class TotalMarketTransactionCountTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        TotalMarketTransactionCount totalMarketTransactionCount = new TotalMarketTransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setCount(20);

        assertEquals(TWITTER, totalMarketTransactionCount.getMarket());
        assertEquals(now, totalMarketTransactionCount.getTimestamp());
        assertEquals(20, totalMarketTransactionCount.getCount());
    }

    @Test
    public void testEquals() {
        TotalMarketTransactionCount totalMarketTransactionCount1 = new TotalMarketTransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setCount(20);
        TotalMarketTransactionCount totalMarketTransactionCount2 = new TotalMarketTransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setCount(20);
        assertEquals(totalMarketTransactionCount1, totalMarketTransactionCount2);
    }

    @Test
    public void testHashCode() {
        TotalMarketTransactionCount totalMarketTransactionCount = new TotalMarketTransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(timestamp)
                .setCount(20);
        assertEquals(29791, new TotalMarketTransactionCount().hashCode());
        assertNotEquals(0, totalMarketTransactionCount.hashCode()); // enums makes the value inconsistent
    }

    @Test
    public void testToString() {
        TotalMarketTransactionCount totalMarketTransactionCount = new TotalMarketTransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setCount(20);
        assertEquals("TotalMarketTransactionCount{market=Twitter, timestamp=" + now + ", count=20}",
                totalMarketTransactionCount.toString());
    }
}
