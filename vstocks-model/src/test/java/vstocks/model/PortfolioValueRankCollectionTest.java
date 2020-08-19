package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.*;
import static vstocks.model.User.generateId;

public class PortfolioValueRankCollectionTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank()
                .setUserId(generateId("user1@domain.com"))
                .setTimestamp(now)
                .setRank(1234);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank()
                .setUserId(generateId("user2@domain.com"))
                .setTimestamp(now)
                .setRank(1234);

        List<PortfolioValueRank> ranks = asList(portfolioValueRank1, portfolioValueRank2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueRankCollection collection = new PortfolioValueRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals(ranks, collection.getRanks());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank()
                .setUserId(generateId("user1@domain.com"))
                .setTimestamp(now)
                .setRank(1234);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank()
                .setUserId(generateId("user2@domain.com"))
                .setTimestamp(now)
                .setRank(1234);

        List<PortfolioValueRank> ranks = asList(portfolioValueRank1, portfolioValueRank2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueRankCollection collection1 = new PortfolioValueRankCollection().setRanks(ranks).setDeltas(deltas);
        PortfolioValueRankCollection collection2 = new PortfolioValueRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank()
                .setUserId(generateId("user1@domain.com"))
                .setTimestamp(now)
                .setRank(1234);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank()
                .setUserId(generateId("user2@domain.com"))
                .setTimestamp(now)
                .setRank(1234);

        List<PortfolioValueRank> ranks = asList(portfolioValueRank1, portfolioValueRank2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueRankCollection collection = new PortfolioValueRankCollection().setRanks(ranks).setDeltas(deltas);
        assertEquals(961, new PortfolioValueRankCollection().hashCode());
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank()
                .setUserId(generateId("user1@domain.com"))
                .setTimestamp(now)
                .setRank(1234);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank()
                .setUserId(generateId("user2@domain.com"))
                .setTimestamp(now)
                .setRank(1234);

        List<PortfolioValueRank> ranks = asList(portfolioValueRank1, portfolioValueRank2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueRankCollection collection = new PortfolioValueRankCollection().setRanks(ranks).setDeltas(deltas);

        assertEquals("PortfolioValueRankCollection{ranks=[" + portfolioValueRank1 + ", "
                + portfolioValueRank2 + "], deltas=" + deltas + "}", collection.toString());
    }
}
