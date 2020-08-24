package vstocks.model.system;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;

public class OverallMarketValueTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        OverallMarketValue overallMarketValue = new OverallMarketValue()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setValue(20);

        assertEquals(TWITTER, overallMarketValue.getMarket());
        assertEquals(now, overallMarketValue.getTimestamp());
        assertEquals(20, overallMarketValue.getValue());
    }

    @Test
    public void testEquals() {
        OverallMarketValue overallMarketValue1 = new OverallMarketValue()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setValue(20);
        OverallMarketValue overallMarketValue2 = new OverallMarketValue()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setValue(20);
        assertEquals(overallMarketValue1, overallMarketValue2);
    }

    @Test
    public void testHashCode() {
        OverallMarketValue overallMarketValue = new OverallMarketValue()
                .setMarket(TWITTER)
                .setTimestamp(timestamp)
                .setValue(20);
        assertEquals(29791, new OverallMarketValue().hashCode());
        assertNotEquals(0, overallMarketValue.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        OverallMarketValue overallMarketValue = new OverallMarketValue()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setValue(20);
        assertEquals("OverallMarketValue{market=Twitter, timestamp=" + now + ", value=20}",
                overallMarketValue.toString());
    }
}
