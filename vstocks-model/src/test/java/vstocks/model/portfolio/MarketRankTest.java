package vstocks.model.portfolio;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;

public class MarketRankTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        MarketRank marketRank = new MarketRank()
                .setBatch(1)
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setRank(20);

        assertEquals(1, marketRank.getBatch());
        assertEquals("userId", marketRank.getUserId());
        assertEquals(TWITTER, marketRank.getMarket());
        assertEquals(now, marketRank.getTimestamp());
        assertEquals(20, marketRank.getRank());
    }

    @Test
    public void testEquals() {
        MarketRank marketRank1 = new MarketRank()
                .setBatch(1)
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setRank(20);
        MarketRank marketRank2 = new MarketRank()
                .setBatch(1)
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setRank(20);
        assertEquals(marketRank1, marketRank2);
    }

    @Test
    public void testHashCode() {
        MarketRank marketRank = new MarketRank()
                .setBatch(1)
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(timestamp)
                .setRank(20);
        assertEquals(28629151, new MarketRank().hashCode());
        assertNotEquals(0, marketRank.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        MarketRank marketRank = new MarketRank()
                .setBatch(1)
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setRank(20);
        assertEquals("MarketRank{batch=1, userId='userId', market=Twitter, timestamp=" + now + ", rank=20}",
                marketRank.toString());
    }
}
