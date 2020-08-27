package vstocks.model.system;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Delta.getDeltas;
import static vstocks.model.Market.TWITTER;

public class TotalMarketTransactionCountCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final TotalMarketTransactionCount totalMarketTransactionCount1 = new TotalMarketTransactionCount()
            .setMarket(TWITTER)
            .setTimestamp(timestamp)
            .setCount(20);
    private final TotalMarketTransactionCount totalMarketTransactionCount2 = new TotalMarketTransactionCount()
            .setMarket(TWITTER)
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);

    private final List<TotalMarketTransactionCount> counts =
            asList(totalMarketTransactionCount1, totalMarketTransactionCount2);
    private final List<Delta> deltas =
            getDeltas(counts, TotalMarketTransactionCount::getTimestamp, TotalMarketTransactionCount::getCount);

    @Test
    public void testGettersAndSetters() {
        TotalMarketTransactionCountCollection collection = new TotalMarketTransactionCountCollection()
                .setMarket(TWITTER)
                .setCounts(counts)
                .setDeltas(deltas);

        assertEquals(counts, collection.getCounts());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        TotalMarketTransactionCountCollection collection1 = new TotalMarketTransactionCountCollection()
                .setMarket(TWITTER)
                .setCounts(counts)
                .setDeltas(deltas);
        TotalMarketTransactionCountCollection collection2 = new TotalMarketTransactionCountCollection()
                .setMarket(TWITTER)
                .setCounts(counts)
                .setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        TotalMarketTransactionCountCollection collection = new TotalMarketTransactionCountCollection()
                .setMarket(TWITTER)
                .setCounts(counts)
                .setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        TotalMarketTransactionCountCollection collection = new TotalMarketTransactionCountCollection()
                .setMarket(TWITTER)
                .setCounts(counts)
                .setDeltas(deltas);
        assertEquals("TotalMarketTransactionCountCollection{market=Twitter, counts="
                        + counts + ", deltas=" + deltas + "}", collection.toString());
    }
}
