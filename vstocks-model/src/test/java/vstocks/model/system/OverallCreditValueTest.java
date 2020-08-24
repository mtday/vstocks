package vstocks.model.system;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class OverallCreditValueTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        OverallCreditValue overallCreditValue = new OverallCreditValue().setTimestamp(now).setValue(20);

        assertEquals(now, overallCreditValue.getTimestamp());
        assertEquals(20, overallCreditValue.getValue());
    }

    @Test
    public void testEquals() {
        OverallCreditValue overallCreditValue1 = new OverallCreditValue().setTimestamp(now).setValue(20);
        OverallCreditValue overallCreditValue2 = new OverallCreditValue().setTimestamp(now).setValue(20);
        assertEquals(overallCreditValue1, overallCreditValue2);
    }

    @Test
    public void testHashCode() {
        OverallCreditValue overallCreditValue = new OverallCreditValue().setTimestamp(timestamp).setValue(20);
        assertEquals(961, new OverallCreditValue().hashCode());
        assertEquals(-1722900141, overallCreditValue.hashCode());
    }

    @Test
    public void testToString() {
        OverallCreditValue overallCreditValue = new OverallCreditValue().setTimestamp(now).setValue(20);
        assertEquals("OverallCreditValue{timestamp=" + now + ", value=20}", overallCreditValue.toString());
    }
}
