package vstocks.model.portfolio;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Delta.getDeltas;

public class MarketTotalRankCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final MarketTotalRank marketTotalRank1 = new MarketTotalRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setRank(20);
    private final MarketTotalRank marketTotalRank2 = new MarketTotalRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setRank(18);

    private final List<MarketTotalRank> ranks = asList(marketTotalRank1, marketTotalRank2);
    private final List<Delta> deltas = getDeltas(ranks, MarketTotalRank::getTimestamp, MarketTotalRank::getRank);

    @Test
    public void testGettersAndSetters() {
        MarketTotalRankCollection collection = new MarketTotalRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals(ranks, collection.getRanks());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        MarketTotalRankCollection collection1 = new MarketTotalRankCollection().setRanks(ranks).setDeltas(deltas);
        MarketTotalRankCollection collection2 = new MarketTotalRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        MarketTotalRankCollection collection = new MarketTotalRankCollection().setRanks(ranks).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        MarketTotalRankCollection collection = new MarketTotalRankCollection().setRanks(ranks).setDeltas(deltas);
        assertEquals("MarketTotalRankCollection{ranks=" + ranks + ", deltas=" + deltas + "}", collection.toString());
    }
}
