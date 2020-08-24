package vstocks.model.system;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class OverallMarketTotalValueTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        OverallMarketTotalValue overallMarketTotalValue = new OverallMarketTotalValue().setTimestamp(now).setValue(20);

        assertEquals(now, overallMarketTotalValue.getTimestamp());
        assertEquals(20, overallMarketTotalValue.getValue());
    }

    @Test
    public void testEquals() {
        OverallMarketTotalValue overallMarketTotalValue1 = new OverallMarketTotalValue().setTimestamp(now).setValue(20);
        OverallMarketTotalValue overallMarketTotalValue2 = new OverallMarketTotalValue().setTimestamp(now).setValue(20);
        assertEquals(overallMarketTotalValue1, overallMarketTotalValue2);
    }

    @Test
    public void testHashCode() {
        OverallMarketTotalValue overallMarketTotalValue = new OverallMarketTotalValue().setTimestamp(timestamp).setValue(20);
        assertEquals(961, new OverallMarketTotalValue().hashCode());
        assertEquals(-1722900141, overallMarketTotalValue.hashCode());
    }

    @Test
    public void testToString() {
        OverallMarketTotalValue overallMarketTotalValue = new OverallMarketTotalValue().setTimestamp(now).setValue(20);
        assertEquals("OverallMarketTotalValue{timestamp=" + now + ", value=20}", overallMarketTotalValue.toString());
    }
}
