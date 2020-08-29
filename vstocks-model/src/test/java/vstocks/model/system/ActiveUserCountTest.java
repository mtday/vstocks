package vstocks.model.system;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class ActiveUserCountTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        ActiveUserCount activeUserCount = new ActiveUserCount()
                .setTimestamp(now)
                .setCount(20);

        assertEquals(now, activeUserCount.getTimestamp());
        assertEquals(20, activeUserCount.getCount());
    }

    @Test
    public void testEquals() {
        ActiveUserCount activeUserCount1 = new ActiveUserCount()
                .setTimestamp(now)
                .setCount(20);
        ActiveUserCount activeUserCount2 = new ActiveUserCount()
                .setTimestamp(now)
                .setCount(20);
        assertEquals(activeUserCount1, activeUserCount2);
    }

    @Test
    public void testHashCode() {
        ActiveUserCount activeUserCount = new ActiveUserCount()
                .setTimestamp(timestamp)
                .setCount(20);
        assertEquals(-1722900141, activeUserCount.hashCode());
    }

    @Test
    public void testToString() {
        ActiveUserCount activeUserCount = new ActiveUserCount()
                .setTimestamp(now)
                .setCount(20);
        assertEquals("ActiveUserCount{timestamp=" + now + ", count=20}", activeUserCount.toString());
    }
}
