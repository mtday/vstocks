package vstocks.model.portfolio;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;

public class MarketValueTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        MarketValue marketValue = new MarketValue()
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setValue(20);

        assertEquals("userId", marketValue.getUserId());
        assertEquals(TWITTER, marketValue.getMarket());
        assertEquals(now, marketValue.getTimestamp());
        assertEquals(20, marketValue.getValue());
    }

    @Test
    public void testEquals() {
        MarketValue marketValue1 = new MarketValue()
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setValue(20);
        MarketValue marketValue2 = new MarketValue()
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setValue(20);
        assertEquals(marketValue1, marketValue2);
    }

    @Test
    public void testHashCode() {
        MarketValue marketValue = new MarketValue()
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(timestamp)
                .setValue(20);
        assertEquals(923521, new MarketValue().hashCode());
        assertNotEquals(0, marketValue.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        MarketValue marketValue = new MarketValue()
                .setUserId("userId")
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setValue(20);
        assertEquals("MarketValue{userId='userId', market=Twitter, timestamp=" + now + ", value=20}",
                marketValue.toString());
    }
}
