package vstocks.model.system;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class TotalUserCountTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        TotalUserCount totalUserCount = new TotalUserCount()
                .setTimestamp(now)
                .setCount(20);

        assertEquals(now, totalUserCount.getTimestamp());
        assertEquals(20, totalUserCount.getCount());
    }

    @Test
    public void testEquals() {
        TotalUserCount totalUserCount1 = new TotalUserCount()
                .setTimestamp(now)
                .setCount(20);
        TotalUserCount totalUserCount2 = new TotalUserCount()
                .setTimestamp(now)
                .setCount(20);
        assertEquals(totalUserCount1, totalUserCount2);
    }

    @Test
    public void testHashCode() {
        TotalUserCount totalUserCount = new TotalUserCount()
                .setTimestamp(timestamp)
                .setCount(20);
        assertEquals(961, new TotalUserCount().hashCode());
        assertEquals(-1722900141, totalUserCount.hashCode());
    }

    @Test
    public void testToString() {
        TotalUserCount totalUserCount = new TotalUserCount()
                .setTimestamp(now)
                .setCount(20);
        assertEquals("TotalUserCount{timestamp=" + now + ", count=20}", totalUserCount.toString());
    }
}
