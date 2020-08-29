package vstocks.model.system;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Delta.getDeltas;

public class TotalUserCountCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final TotalUserCount totalUserCount1 = new TotalUserCount()
            .setTimestamp(timestamp)
            .setCount(20);
    private final TotalUserCount totalUserCount2 = new TotalUserCount()
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);

    private final List<TotalUserCount> counts = asList(totalUserCount1, totalUserCount2);
    private final List<Delta> deltas = getDeltas(counts, TotalUserCount::getTimestamp, TotalUserCount::getCount);

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
        assertEquals(-259824333, collection.hashCode());
    }

    @Test
    public void testToString() {
        TotalUserCountCollection collection = new TotalUserCountCollection().setCounts(counts).setDeltas(deltas);
        assertEquals("TotalUserCountCollection{counts=" + counts + ", deltas=" + deltas + "}", collection.toString());
    }
}
