package vstocks.model.portfolio;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
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
                .setRank(20)
                .setValue(10);

        assertEquals(1, marketRank.getBatch());
        assertEquals("userId", marketRank.getUserId());
        assertEquals(TWITTER, marketRank.getMarket());
        assertEquals(now, marketRank.getTimestamp());
        assertEquals(20, marketRank.getRank());
        assertEquals(10, marketRank.getValue());
    }

    @Test
    public void testEquals() {
        MarketRank marketRank1 = new MarketRank()
                .setBatch(1)
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);
        MarketRank marketRank2 = new MarketRank()
                .setBatch(1)
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);
        assertEquals(marketRank1, marketRank2);
    }

    @Test
    public void testHashCode() {
        MarketRank marketRank = new MarketRank()
                .setBatch(1)
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(timestamp)
                .setRank(20)
                .setValue(10);
        assertEquals(759400619, marketRank.hashCode());
    }

    @Test
    public void testToString() {
        MarketRank marketRank = new MarketRank()
                .setBatch(1)
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);
        assertEquals("MarketRank{batch=1, userId='userId', market=Twitter, timestamp=" + now + ", rank=20, value=10}",
                marketRank.toString());
    }
}
