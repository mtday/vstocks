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
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);

        assertEquals("userId", marketTotalRank.getUserId());
        assertEquals(now, marketTotalRank.getTimestamp());
        assertEquals(20, marketTotalRank.getRank());
    }

    @Test
    public void testEquals() {
        MarketTotalRank marketTotalRank1 = new MarketTotalRank()
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);
        MarketTotalRank marketTotalRank2 = new MarketTotalRank()
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);
        assertEquals(marketTotalRank1, marketTotalRank2);
    }

    @Test
    public void testHashCode() {
        MarketTotalRank marketTotalRank = new MarketTotalRank()
                .setUserId("userId")
                .setTimestamp(timestamp)
                .setRank(20);
        assertEquals(29791, new MarketTotalRank().hashCode());
        assertEquals(-1989687625, marketTotalRank.hashCode());
    }

    @Test
    public void testToString() {
        MarketTotalRank marketTotalRank = new MarketTotalRank()
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);
        assertEquals("MarketTotalRank{userId='userId', timestamp=" + now + ", rank=20}",
                marketTotalRank.toString());
    }
}
