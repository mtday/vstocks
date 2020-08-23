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

public class ActiveTransactionCountCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final ActiveTransactionCount activeTransactionCount1 = new ActiveTransactionCount()
            .setTimestamp(timestamp)
            .setCount(20);
    private final ActiveTransactionCount activeTransactionCount2 = new ActiveTransactionCount()
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);

    private final List<ActiveTransactionCount> counts = asList(activeTransactionCount1, activeTransactionCount2);
    private final Map<DeltaInterval, Delta> deltas =
            Delta.getDeltas(counts, ActiveTransactionCount::getTimestamp, ActiveTransactionCount::getCount);

    @Test
    public void testGettersAndSetters() {
        ActiveTransactionCountCollection collection = new ActiveTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);

        assertEquals(counts, collection.getCounts());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        ActiveTransactionCountCollection collection1 = new ActiveTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);
        ActiveTransactionCountCollection collection2 = new ActiveTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        ActiveTransactionCountCollection collection = new ActiveTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        ActiveTransactionCountCollection collection = new ActiveTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);
        assertEquals("ActiveTransactionCountCollection{counts=" + counts + ", deltas=" + deltas + "}",
                collection.toString());
    }
}
