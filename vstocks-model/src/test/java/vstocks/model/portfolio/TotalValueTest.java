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
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);

        assertEquals(1, totalValue.getBatch());
        assertEquals("userId", totalValue.getUserId());
        assertEquals(now, totalValue.getTimestamp());
        assertEquals(20, totalValue.getValue());
    }

    @Test
    public void testEquals() {
        TotalValue totalValue1 = new TotalValue()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        TotalValue totalValue2 = new TotalValue()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        assertEquals(totalValue1, totalValue2);
    }

    @Test
    public void testHashCode() {
        TotalValue totalValue = new TotalValue()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(timestamp)
                .setValue(20);
        assertEquals(923521, new TotalValue().hashCode());
        assertEquals(-1988764104, totalValue.hashCode());
    }

    @Test
    public void testToString() {
        TotalValue totalValue = new TotalValue()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        assertEquals("TotalValue{batch=1, userId='userId', timestamp=" + now + ", value=20}",
                totalValue.toString());
    }
}
