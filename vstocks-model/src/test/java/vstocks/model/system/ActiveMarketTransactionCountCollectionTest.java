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
import static vstocks.model.Market.TWITTER;

public class ActiveMarketTransactionCountCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final ActiveMarketTransactionCount activeMarketTransactionCount1 = new ActiveMarketTransactionCount()
            .setMarket(TWITTER)
            .setTimestamp(timestamp)
            .setCount(20);
    private final ActiveMarketTransactionCount activeMarketTransactionCount2 = new ActiveMarketTransactionCount()
            .setMarket(TWITTER)
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);

    private final List<ActiveMarketTransactionCount> counts =
            asList(activeMarketTransactionCount1, activeMarketTransactionCount2);
    private final Map<DeltaInterval, Delta> deltas =
            Delta.getDeltas(counts, ActiveMarketTransactionCount::getTimestamp, ActiveMarketTransactionCount::getCount);

    @Test
    public void testGettersAndSetters() {
        ActiveMarketTransactionCountCollection collection = new ActiveMarketTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);

        assertEquals(counts, collection.getCounts());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        ActiveMarketTransactionCountCollection collection1 = new ActiveMarketTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);
        ActiveMarketTransactionCountCollection collection2 = new ActiveMarketTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        ActiveMarketTransactionCountCollection collection = new ActiveMarketTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        ActiveMarketTransactionCountCollection collection = new ActiveMarketTransactionCountCollection()
                .setCounts(counts)
                .setDeltas(deltas);
        assertEquals("ActiveMarketTransactionCountCollection{counts=" + counts + ", deltas=" + deltas + "}",
                collection.toString());
    }
}
