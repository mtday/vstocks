package vstocks.model.portfolio;

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

public class MarketRankCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final MarketRank marketRank1 = new MarketRank()
            .setBatch(1)
            .setUserId("userId")
            .setMarket(TWITTER)
            .setTimestamp(timestamp)
            .setRank(20);
    private final MarketRank marketRank2 = new MarketRank()
            .setBatch(1)
            .setUserId("userId")
            .setMarket(TWITTER)
            .setTimestamp(timestamp.minusSeconds(10))
            .setRank(18);

    private final List<MarketRank> ranks = asList(marketRank1, marketRank2);
    private final Map<DeltaInterval, Delta> deltas =
            Delta.getDeltas(ranks, MarketRank::getTimestamp, MarketRank::getRank);

    @Test
    public void testGettersAndSetters() {
        MarketRankCollection collection = new MarketRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals(ranks, collection.getRanks());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        MarketRankCollection collection1 = new MarketRankCollection().setRanks(ranks).setDeltas(deltas);
        MarketRankCollection collection2 = new MarketRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        MarketRankCollection collection = new MarketRankCollection().setRanks(ranks).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        MarketRankCollection collection = new MarketRankCollection().setRanks(ranks).setDeltas(deltas);
        assertEquals("MarketRankCollection{ranks=" + ranks + ", deltas=" + deltas + "}", collection.toString());
    }
}
