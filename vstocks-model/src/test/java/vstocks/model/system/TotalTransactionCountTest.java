package vstocks.model.system;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class TotalTransactionCountTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        TotalTransactionCount totalTransactionCount = new TotalTransactionCount()
                .setTimestamp(now)
                .setCount(20);

        assertEquals(now, totalTransactionCount.getTimestamp());
        assertEquals(20, totalTransactionCount.getCount());
    }

    @Test
    public void testEquals() {
        TotalTransactionCount totalTransactionCount1 = new TotalTransactionCount()
                .setTimestamp(now)
                .setCount(20);
        TotalTransactionCount totalTransactionCount2 = new TotalTransactionCount()
                .setTimestamp(now)
                .setCount(20);
        assertEquals(totalTransactionCount1, totalTransactionCount2);
    }

    @Test
    public void testHashCode() {
        TotalTransactionCount totalTransactionCount = new TotalTransactionCount()
                .setTimestamp(timestamp)
                .setCount(20);
        assertEquals(961, new TotalTransactionCount().hashCode());
        assertEquals(-1722900141, totalTransactionCount.hashCode());
    }

    @Test
    public void testToString() {
        TotalTransactionCount totalTransactionCount = new TotalTransactionCount()
                .setTimestamp(now)
                .setCount(20);
        assertEquals("TotalTransactionCount{timestamp=" + now + ", count=20}", totalTransactionCount.toString());
    }
}
