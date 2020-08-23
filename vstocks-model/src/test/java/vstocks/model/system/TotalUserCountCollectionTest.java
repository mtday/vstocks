package vstocks.model.system;

import org.junit.Test;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class TotalUserCountCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final TotalUserCount totalUserCount1 = new TotalUserCount()
            .setTimestamp(timestamp)
            .setCount(20);
    private final TotalUserCount totalUserCount2 = new TotalUserCount()
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);

    private final List<TotalUserCount> counts = asList(totalUserCount1, totalUserCount2);
    private final Map<DeltaInterval, Delta> deltas =
            Delta.getDeltas(counts, TotalUserCount::getTimestamp, TotalUserCount::getCount);

    @Test
    public void testGettersAndSetters() {
        TotalUserCountCollection collection = new TotalUserCountCollection().setCounts(counts).setDeltas(deltas);

        assertEquals(counts, collection.getCounts());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        TotalUserCountCollection collection1 = new TotalUserCountCollection().setCounts(counts).setDeltas(deltas);
        TotalUserCountCollection collection2 = new TotalUserCountCollection().setCounts(counts).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        TotalUserCountCollection collection = new TotalUserCountCollection().setCounts(counts).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        TotalUserCountCollection collection = new TotalUserCountCollection().setCounts(counts).setDeltas(deltas);
        assertEquals("TotalUserCountCollection{counts=" + counts + ", deltas=" + deltas + "}", collection.toString());
    }
}
