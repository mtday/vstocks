package vstocks.model.system;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class OverallTotalValueTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        OverallTotalValue overallTotalValue = new OverallTotalValue().setTimestamp(now).setValue(20);

        assertEquals(now, overallTotalValue.getTimestamp());
        assertEquals(20, overallTotalValue.getValue());
    }

    @Test
    public void testEquals() {
        OverallTotalValue overallTotalValue1 = new OverallTotalValue().setTimestamp(now).setValue(20);
        OverallTotalValue overallTotalValue2 = new OverallTotalValue().setTimestamp(now).setValue(20);
        assertEquals(overallTotalValue1, overallTotalValue2);
    }

    @Test
    public void testHashCode() {
        OverallTotalValue overallTotalValue = new OverallTotalValue().setTimestamp(timestamp).setValue(20);
        assertEquals(-1722900141, overallTotalValue.hashCode());
    }

    @Test
    public void testToString() {
        OverallTotalValue overallTotalValue = new OverallTotalValue().setTimestamp(now).setValue(20);
        assertEquals("OverallTotalValue{timestamp=" + now + ", value=20}", overallTotalValue.toString());
    }
}
