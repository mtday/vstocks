package vstocks.model.portfolio;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class MarketTotalRankTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        MarketTotalRank marketTotalRank = new MarketTotalRank()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);

        assertEquals(1, marketTotalRank.getBatch());
        assertEquals("userId", marketTotalRank.getUserId());
        assertEquals(now, marketTotalRank.getTimestamp());
        assertEquals(20, marketTotalRank.getRank());
        assertEquals(10, marketTotalRank.getValue());
    }

    @Test
    public void testEquals() {
        MarketTotalRank marketTotalRank1 = new MarketTotalRank()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);
        MarketTotalRank marketTotalRank2 = new MarketTotalRank()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);
        assertEquals(marketTotalRank1, marketTotalRank2);
    }

    @Test
    public void testHashCode() {
        MarketTotalRank marketTotalRank = new MarketTotalRank()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(timestamp)
                .setRank(20)
                .setValue(10);
        assertEquals(-1522145070, marketTotalRank.hashCode());
    }

    @Test
    public void testToString() {
        MarketTotalRank marketTotalRank = new MarketTotalRank()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);
        assertEquals("MarketTotalRank{batch=1, userId='userId', timestamp=" + now + ", rank=20, value=10}",
                marketTotalRank.toString());
    }
}
