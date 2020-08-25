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
import static vstocks.model.Delta.getDeltas;

public class TotalRankCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final TotalRank totalRank1 = new TotalRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setRank(20);
    private final TotalRank totalRank2 = new TotalRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setRank(18);

    private final List<TotalRank> ranks = asList(totalRank1, totalRank2);
    private final Map<DeltaInterval, Delta> deltas = getDeltas(ranks, TotalRank::getTimestamp, TotalRank::getRank);

    @Test
    public void testGettersAndSetters() {
        TotalRankCollection collection = new TotalRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals(ranks, collection.getRanks());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        TotalRankCollection collection1 = new TotalRankCollection().setRanks(ranks).setDeltas(deltas);
        TotalRankCollection collection2 = new TotalRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        TotalRankCollection collection = new TotalRankCollection().setRanks(ranks).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        TotalRankCollection collection = new TotalRankCollection().setRanks(ranks).setDeltas(deltas);
        assertEquals("TotalRankCollection{ranks=" + ranks + ", deltas=" + deltas + "}", collection.toString());
    }
}
