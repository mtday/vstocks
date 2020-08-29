package vstocks.model.portfolio;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Delta.getDeltas;

public class CreditRankCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final CreditRank creditRank1 = new CreditRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setRank(20);
    private final CreditRank creditRank2 = new CreditRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setRank(18);

    private final List<CreditRank> ranks = asList(creditRank1, creditRank2);
    private final List<Delta> deltas = getDeltas(ranks, CreditRank::getTimestamp, CreditRank::getRank);

    @Test
    public void testGettersAndSetters() {
        CreditRankCollection collection = new CreditRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals(ranks, collection.getRanks());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        CreditRankCollection collection1 = new CreditRankCollection().setRanks(ranks).setDeltas(deltas);
        CreditRankCollection collection2 = new CreditRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        CreditRankCollection collection = new CreditRankCollection().setRanks(ranks).setDeltas(deltas);
        assertEquals(1320410403, collection.hashCode());
    }

    @Test
    public void testToString() {
        CreditRankCollection collection = new CreditRankCollection().setRanks(ranks).setDeltas(deltas);
        assertEquals("CreditRankCollection{ranks=" + ranks + ", deltas=" + deltas + "}", collection.toString());
    }
}
