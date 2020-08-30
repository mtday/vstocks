package vstocks.model.portfolio;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Delta.getDeltas;
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
    private final List<Delta> deltas = getDeltas(ranks, MarketRank::getTimestamp, MarketRank::getRank);

    @Test
    public void testGettersAndSetters() {
        MarketRankCollection collection =
                new MarketRankCollection().setMarket(TWITTER).setRanks(ranks).setDeltas(deltas);

        assertEquals(TWITTER, collection.getMarket());
        assertEquals(ranks, collection.getRanks());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        MarketRankCollection collection1 =
                new MarketRankCollection().setMarket(TWITTER).setRanks(ranks).setDeltas(deltas);
        MarketRankCollection collection2 =
                new MarketRankCollection().setMarket(TWITTER).setRanks(ranks).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        MarketRankCollection collection =
                new MarketRankCollection().setMarket(TWITTER).setRanks(ranks).setDeltas(deltas);
        assertEquals(-482827212, collection.hashCode());
    }

    @Test
    public void testToString() {
        MarketRankCollection collection =
                new MarketRankCollection().setMarket(TWITTER).setRanks(ranks).setDeltas(deltas);
        assertEquals("MarketRankCollection{market=Twitter, ranks=" + ranks + ", deltas=" + deltas + "}",
                collection.toString());
    }
}
