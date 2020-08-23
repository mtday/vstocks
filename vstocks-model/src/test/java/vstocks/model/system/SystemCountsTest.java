package vstocks.model.system;

import org.junit.Test;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.Market;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;
import static vstocks.model.Market.TWITTER;

public class SystemCountsTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final ActiveUserCount activeUserCount1 = new ActiveUserCount()
            .setTimestamp(timestamp)
            .setCount(20);
    private final ActiveUserCount activeUserCount2 = new ActiveUserCount()
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);
    private final List<ActiveUserCount> activeUserCounts = asList(activeUserCount1, activeUserCount2);
    private final Map<DeltaInterval, Delta> activeUserCountDeltas =
            Delta.getDeltas(activeUserCounts, ActiveUserCount::getTimestamp, ActiveUserCount::getCount);
    private final ActiveUserCountCollection activeUserCountCollection = new ActiveUserCountCollection()
            .setCounts(activeUserCounts)
            .setDeltas(activeUserCountDeltas);

    private final TotalUserCount totalUserCount1 = new TotalUserCount()
            .setTimestamp(timestamp)
            .setCount(20);
    private final TotalUserCount totalUserCount2 = new TotalUserCount()
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);
    private final List<TotalUserCount> totalUserCounts = asList(totalUserCount1, totalUserCount2);
    private final Map<DeltaInterval, Delta> totalUserCountDeltas =
            Delta.getDeltas(totalUserCounts, TotalUserCount::getTimestamp, TotalUserCount::getCount);
    private final TotalUserCountCollection totalUserCountCollection = new TotalUserCountCollection()
            .setCounts(totalUserCounts)
            .setDeltas(totalUserCountDeltas);

    private final ActiveTransactionCount activeTxCount1 = new ActiveTransactionCount()
            .setTimestamp(timestamp)
            .setCount(20);
    private final ActiveTransactionCount activeTxCount2 = new ActiveTransactionCount()
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);
    private final List<ActiveTransactionCount> activeTxCounts = asList(activeTxCount1, activeTxCount2);
    private final Map<DeltaInterval, Delta> activeTxCountDeltas =
            Delta.getDeltas(activeTxCounts, ActiveTransactionCount::getTimestamp, ActiveTransactionCount::getCount);
    private final ActiveTransactionCountCollection activeTxCountCollection = new ActiveTransactionCountCollection()
            .setCounts(activeTxCounts)
            .setDeltas(activeTxCountDeltas);

    private final TotalTransactionCount totalTxCount1 = new TotalTransactionCount()
            .setTimestamp(timestamp)
            .setCount(20);
    private final TotalTransactionCount totalTxCount2 = new TotalTransactionCount()
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);
    private final List<TotalTransactionCount> totalTxCounts = asList(totalTxCount1, totalTxCount2);
    private final Map<DeltaInterval, Delta> totalTxCountDeltas = Delta.getDeltas(
            totalTxCounts, TotalTransactionCount::getTimestamp, TotalTransactionCount::getCount);
    private final TotalTransactionCountCollection totalTxCountCollection = new TotalTransactionCountCollection()
            .setCounts(totalTxCounts)
            .setDeltas(totalTxCountDeltas);

    private final ActiveMarketTransactionCount activeMarketTxCount1 = new ActiveMarketTransactionCount()
            .setMarket(TWITTER)
            .setTimestamp(timestamp)
            .setCount(20);
    private final ActiveMarketTransactionCount activeMarketTxCount2 = new ActiveMarketTransactionCount()
            .setMarket(TWITTER)
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);
    private final List<ActiveMarketTransactionCount> activeMarketTxCounts =
            asList(activeMarketTxCount1, activeMarketTxCount2);
    private final Map<DeltaInterval, Delta> activeMarketTxCountDeltas = Delta.getDeltas(activeMarketTxCounts,
            ActiveMarketTransactionCount::getTimestamp, ActiveMarketTransactionCount::getCount);
    private final ActiveMarketTransactionCountCollection activeMarketTxCountCollection =
            new ActiveMarketTransactionCountCollection()
            .setCounts(activeMarketTxCounts)
            .setDeltas(activeMarketTxCountDeltas);
    private final Map<Market, ActiveMarketTransactionCountCollection> activeMarketTxCountCollectionMap =
            singletonMap(TWITTER, activeMarketTxCountCollection);

    private final TotalMarketTransactionCount totalMarketTxCount1 = new TotalMarketTransactionCount()
            .setMarket(TWITTER)
            .setTimestamp(timestamp)
            .setCount(20);
    private final TotalMarketTransactionCount totalMarketTxCount2 = new TotalMarketTransactionCount()
            .setMarket(TWITTER)
            .setTimestamp(timestamp.minusSeconds(10))
            .setCount(18);
    private final List<TotalMarketTransactionCount> totalMarketTxCounts =
            asList(totalMarketTxCount1, totalMarketTxCount2);
    private final Map<DeltaInterval, Delta> totalMarketTxCountDeltas = Delta.getDeltas(totalMarketTxCounts,
            TotalMarketTransactionCount::getTimestamp, TotalMarketTransactionCount::getCount);
    private final TotalMarketTransactionCountCollection totalMarketTxCountCollection =
            new TotalMarketTransactionCountCollection()
                    .setCounts(totalMarketTxCounts)
                    .setDeltas(totalMarketTxCountDeltas);
    private final Map<Market, TotalMarketTransactionCountCollection> totalMarketTxCountCollectionMap =
            singletonMap(TWITTER, totalMarketTxCountCollection);

    @Test
    public void testGettersAndSetters() {
        SystemCounts systemCounts = new SystemCounts()
                .setActiveUserCounts(activeUserCountCollection)
                .setTotalUserCounts(totalUserCountCollection)
                .setActiveTransactionCounts(activeTxCountCollection)
                .setTotalTransactionCounts(totalTxCountCollection)
                .setActiveMarketTransactionCounts(activeMarketTxCountCollectionMap)
                .setTotalMarketTransactionCounts(totalMarketTxCountCollectionMap);

        assertEquals(activeUserCountCollection, systemCounts.getActiveUserCounts());
        assertEquals(totalUserCountCollection, systemCounts.getTotalUserCounts());
        assertEquals(activeTxCountCollection, systemCounts.getActiveTransactionCounts());
        assertEquals(totalTxCountCollection, systemCounts.getTotalTransactionCounts());
        assertEquals(activeMarketTxCountCollectionMap, systemCounts.getActiveMarketTransactionCounts());
        assertEquals(totalMarketTxCountCollectionMap, systemCounts.getTotalMarketTransactionCounts());
    }

    @Test
    public void testEquals() {
        SystemCounts systemCounts1 = new SystemCounts()
                .setActiveUserCounts(activeUserCountCollection)
                .setTotalUserCounts(totalUserCountCollection)
                .setActiveTransactionCounts(activeTxCountCollection)
                .setTotalTransactionCounts(totalTxCountCollection)
                .setActiveMarketTransactionCounts(activeMarketTxCountCollectionMap)
                .setTotalMarketTransactionCounts(totalMarketTxCountCollectionMap);
        SystemCounts systemCounts2 = new SystemCounts()
                .setActiveUserCounts(activeUserCountCollection)
                .setTotalUserCounts(totalUserCountCollection)
                .setActiveTransactionCounts(activeTxCountCollection)
                .setTotalTransactionCounts(totalTxCountCollection)
                .setActiveMarketTransactionCounts(activeMarketTxCountCollectionMap)
                .setTotalMarketTransactionCounts(totalMarketTxCountCollectionMap);
        assertEquals(systemCounts1, systemCounts2);
    }

    @Test
    public void testHashCode() {
        SystemCounts systemCounts = new SystemCounts()
                .setActiveUserCounts(activeUserCountCollection)
                .setTotalUserCounts(totalUserCountCollection)
                .setActiveTransactionCounts(activeTxCountCollection)
                .setTotalTransactionCounts(totalTxCountCollection)
                .setActiveMarketTransactionCounts(activeMarketTxCountCollectionMap)
                .setTotalMarketTransactionCounts(totalMarketTxCountCollectionMap);
        assertEquals(887503681, new SystemCounts().hashCode());
        assertNotEquals(0, systemCounts.hashCode()); // enums cause inconsistent values
    }

    @Test
    public void testToString() {
        SystemCounts systemCounts = new SystemCounts()
                .setActiveUserCounts(activeUserCountCollection)
                .setTotalUserCounts(totalUserCountCollection)
                .setActiveTransactionCounts(activeTxCountCollection)
                .setTotalTransactionCounts(totalTxCountCollection)
                .setActiveMarketTransactionCounts(activeMarketTxCountCollectionMap)
                .setTotalMarketTransactionCounts(totalMarketTxCountCollectionMap);
        assertNotNull(systemCounts.toString()); // skipping real check
    }
}
