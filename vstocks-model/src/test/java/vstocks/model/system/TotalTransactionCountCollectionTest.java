package vstocks.model.system;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Delta.getDeltas;

public class TotalTransactionCountCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final TotalTransactionCount totalTransactionCount1 = new TotalTransactionCount()
            .setTimestamp(timestamp)
            .setCount(20);
    private final TotalTransactionCount totalTransactionCount2 = new TotalTransactionCount()
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);

    private final List<TotalTransactionCount> counts = asList(totalTransactionCount1, totalTransactionCount2);
    private final List<Delta> deltas =
            getDeltas(counts, TotalTransactionCount::getTimestamp, TotalTransactionCount::getCount);

    @Test
    public void testGettersAndSetters() {
        TotalTransactionCountCollection collection = new TotalTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);

        assertEquals(counts, collection.getCounts());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        TotalTransactionCountCollection collection1 = new TotalTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);
        TotalTransactionCountCollection collection2 = new TotalTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        TotalTransactionCountCollection collection = new TotalTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);
        assertEquals(-1186596106, collection.hashCode());
    }

    @Test
    public void testToString() {
        TotalTransactionCountCollection collection = new TotalTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);
        assertEquals("TotalTransactionCountCollection{counts=" + counts + ", deltas=" + deltas + "}",
                collection.toString());
    }
}
