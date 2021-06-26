package vstocks.model.system;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Delta.getDeltas;

public class ActiveUserCountCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final ActiveUserCount activeUserCount1 = new ActiveUserCount()
            .setTimestamp(timestamp)
            .setCount(20);
    private final ActiveUserCount activeUserCount2 = new ActiveUserCount()
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);

    private final List<ActiveUserCount> counts = asList(activeUserCount1, activeUserCount2);
    private final List<Delta> deltas = getDeltas(counts, ActiveUserCount::getTimestamp, ActiveUserCount::getCount);

    @Test
    public void testGettersAndSetters() {
        ActiveUserCountCollection collection = new ActiveUserCountCollection().setCounts(counts).setDeltas(deltas);

        assertEquals(counts, collection.getCounts());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        ActiveUserCountCollection collection1 = new ActiveUserCountCollection().setCounts(counts).setDeltas(deltas);
        ActiveUserCountCollection collection2 = new ActiveUserCountCollection().setCounts(counts).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        ActiveUserCountCollection collection = new ActiveUserCountCollection().setCounts(counts).setDeltas(deltas);
        assertEquals(-1186596106, collection.hashCode());
    }

    @Test
    public void testToString() {
        ActiveUserCountCollection collection = new ActiveUserCountCollection().setCounts(counts).setDeltas(deltas);
        assertEquals("ActiveUserCountCollection{counts=" + counts + ", deltas=" + deltas + "}", collection.toString());
    }
}
