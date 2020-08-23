package vstocks.model.portfolio;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class CreditValueTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        CreditValue creditValue = new CreditValue()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);

        assertEquals(1, creditValue.getBatch());
        assertEquals("userId", creditValue.getUserId());
        assertEquals(now, creditValue.getTimestamp());
        assertEquals(20, creditValue.getValue());
    }

    @Test
    public void testEquals() {
        CreditValue creditValue1 = new CreditValue()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        CreditValue creditValue2 = new CreditValue()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        assertEquals(creditValue1, creditValue2);
    }

    @Test
    public void testHashCode() {
        CreditValue creditValue = new CreditValue()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(timestamp)
                .setValue(20);
        assertEquals(923521, new CreditValue().hashCode());
        assertEquals(-1988764104, creditValue.hashCode());
    }

    @Test
    public void testToString() {
        CreditValue creditValue = new CreditValue()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setValue(20);
        assertEquals("CreditValue{batch=1, userId='userId', timestamp=" + now + ", value=20}",
                creditValue.toString());
    }
}
