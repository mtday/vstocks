package vstocks.model.portfolio;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class MarketTotalValueTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        MarketTotalValue marketTotalValue = new MarketTotalValue()
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);

        assertEquals("userId", marketTotalValue.getUserId());
        assertEquals(now, marketTotalValue.getTimestamp());
        assertEquals(20, marketTotalValue.getValue());
    }

    @Test
    public void testEquals() {
        MarketTotalValue marketTotalValue1 = new MarketTotalValue()
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        MarketTotalValue marketTotalValue2 = new MarketTotalValue()
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        assertEquals(marketTotalValue1, marketTotalValue2);
    }

    @Test
    public void testHashCode() {
        MarketTotalValue marketTotalValue = new MarketTotalValue()
                .setUserId("userId")
                .setTimestamp(timestamp)
                .setValue(20);
        assertEquals(29791, new MarketTotalValue().hashCode());
        assertEquals(-1989687625, marketTotalValue.hashCode());
    }

    @Test
    public void testToString() {
        MarketTotalValue marketTotalValue = new MarketTotalValue()
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        assertEquals("MarketTotalValue{userId='userId', timestamp=" + now + ", value=20}",
                marketTotalValue.toString());
    }
}
