package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.*;

public class UserCountCollectionTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserCount userCount1 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(1234L);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(1230L);
        List<UserCount> userCounts = asList(userCount1, userCount2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        UserCountCollection userCountCollection = new UserCountCollection().setUserCounts(userCounts).setDeltas(deltas);

        assertEquals(userCounts, userCountCollection.getUserCounts());
        assertEquals(deltas, userCountCollection.getDeltas());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserCount userCount1 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(1234L);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(1230L);
        List<UserCount> userCounts = asList(userCount1, userCount2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        UserCountCollection userCountCollection1 = new UserCountCollection().setUserCounts(userCounts).setDeltas(deltas);
        UserCountCollection userCountCollection2 = new UserCountCollection().setUserCounts(userCounts).setDeltas(deltas);
        assertEquals(userCountCollection1, userCountCollection2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserCount userCount1 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(1234L);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(1230L);
        List<UserCount> userCounts = asList(userCount1, userCount2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        UserCountCollection userCountCollection = new UserCountCollection().setUserCounts(userCounts).setDeltas(deltas);

        assertNotEquals(0, new UserCountCollection().hashCode()); // enums make the value inconsistent
        assertNotEquals(0, userCountCollection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserCount userCount1 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(1234L);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(1230L);
        List<UserCount> userCounts = asList(userCount1, userCount2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        UserCountCollection userCountCollection = new UserCountCollection().setUserCounts(userCounts).setDeltas(deltas);

        assertEquals("UserCountCollection{userCounts=[" + userCount1.toString() + ", " + userCount2.toString() + "], "
                + "deltas={6h=Delta{interval=6h, change=5, percent=5.25}, "
                + "12h=Delta{interval=12h, change=5, percent=5.25}, 1d=Delta{interval=1d, change=10, percent=10.25}}}",
                userCountCollection.toString());
    }
}
