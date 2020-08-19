package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.*;

public class UserCountTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));
        UserCount userCount = new UserCount()
                .setTimestamp(now)
                .setUsers(1234L)
                .setDeltas(deltas);

        assertEquals(now, userCount.getTimestamp());
        assertEquals(1234, userCount.getUsers());
        assertEquals(deltas, userCount.getDeltas());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserCount userCount1 = new UserCount()
                .setTimestamp(now)
                .setUsers(1234L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        UserCount userCount2 = new UserCount()
                .setTimestamp(now)
                .setUsers(1234L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals(userCount1, userCount2);
    }

    @Test
    public void testHashCode() {
        UserCount userCount = new UserCount()
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setUsers(1234L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertNotEquals(0, new UserCount().hashCode()); // enums make the value inconsistent
        assertNotEquals(0, userCount.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserCount userCount = new UserCount()
                .setTimestamp(now)
                .setUsers(1234L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals("UserCount{timestamp=" + now + ", users=1234, deltas={6h=Delta{interval=6h, change=5, "
                + "percent=5.25}, 12h=Delta{interval=12h, change=5, percent=5.25}, 1d=Delta{interval=1d, "
                + "change=10, percent=10.25}}}", userCount.toString());
    }
}
