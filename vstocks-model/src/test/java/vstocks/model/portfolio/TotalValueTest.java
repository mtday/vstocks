package vstocks.model.portfolio;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class TotalValueTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        TotalValue totalValue = new TotalValue()
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);

        assertEquals("userId", totalValue.getUserId());
        assertEquals(now, totalValue.getTimestamp());
        assertEquals(20, totalValue.getValue());
    }

    @Test
    public void testEquals() {
        TotalValue totalValue1 = new TotalValue()
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        TotalValue totalValue2 = new TotalValue()
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        assertEquals(totalValue1, totalValue2);
    }

    @Test
    public void testHashCode() {
        TotalValue totalValue = new TotalValue()
                .setUserId("userId")
                .setTimestamp(timestamp)
                .setValue(20);
        assertEquals(29791, new TotalValue().hashCode());
        assertEquals(-1989687625, totalValue.hashCode());
    }

    @Test
    public void testToString() {
        TotalValue totalValue = new TotalValue()
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        assertEquals("TotalValue{userId='userId', timestamp=" + now + ", value=20}",
                totalValue.toString());
    }
}
