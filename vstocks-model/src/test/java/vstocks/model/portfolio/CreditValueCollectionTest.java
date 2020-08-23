package vstocks.model.portfolio;

import org.junit.Test;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class CreditValueCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final CreditValue creditValue1 = new CreditValue()
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setValue(20);
    private final CreditValue creditValue2 = new CreditValue()
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);

    private final List<CreditValue> values = asList(creditValue1, creditValue2);
    private final Map<DeltaInterval, Delta> deltas =
            Delta.getDeltas(values, CreditValue::getTimestamp, CreditValue::getValue);

    @Test
    public void testGettersAndSetters() {
        CreditValueCollection collection = new CreditValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(values, collection.getValues());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        CreditValueCollection collection1 = new CreditValueCollection().setValues(values).setDeltas(deltas);
        CreditValueCollection collection2 = new CreditValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        CreditValueCollection collection = new CreditValueCollection().setValues(values).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        CreditValueCollection collection = new CreditValueCollection().setValues(values).setDeltas(deltas);
        assertEquals("CreditValueCollection{values=" + values + ", deltas=" + deltas + "}", collection.toString());
    }
}
