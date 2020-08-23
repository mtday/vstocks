package vstocks.model.system;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class ActiveTransactionCountTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        ActiveTransactionCount activeTransactionCount = new ActiveTransactionCount()
                .setTimestamp(now)
                .setCount(20);

        assertEquals(now, activeTransactionCount.getTimestamp());
        assertEquals(20, activeTransactionCount.getCount());
    }

    @Test
    public void testEquals() {
        ActiveTransactionCount activeTransactionCount1 = new ActiveTransactionCount()
                .setTimestamp(now)
                .setCount(20);
        ActiveTransactionCount activeTransactionCount2 = new ActiveTransactionCount()
                .setTimestamp(now)
                .setCount(20);
        assertEquals(activeTransactionCount1, activeTransactionCount2);
    }

    @Test
    public void testHashCode() {
        ActiveTransactionCount activeTransactionCount = new ActiveTransactionCount()
                .setTimestamp(timestamp)
                .setCount(20);
        assertEquals(961, new ActiveTransactionCount().hashCode());
        assertEquals(-1722900141, activeTransactionCount.hashCode());
    }

    @Test
    public void testToString() {
        ActiveTransactionCount activeTransactionCount = new ActiveTransactionCount()
                .setTimestamp(now)
                .setCount(20);
        assertEquals("ActiveTransactionCount{timestamp=" + now + ", count=20}", activeTransactionCount.toString());
    }
}
