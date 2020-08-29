package vstocks.model.system;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Delta.getDeltas;

public class OverallCreditValueCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final OverallCreditValue overallCreditValue1 = new OverallCreditValue().setTimestamp(timestamp).setValue(20);
    private final OverallCreditValue overallCreditValue2 = new OverallCreditValue()
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);

    private final List<OverallCreditValue> values = asList(overallCreditValue1, overallCreditValue2);
    private final List<Delta> deltas = getDeltas(values, OverallCreditValue::getTimestamp, OverallCreditValue::getValue);

    @Test
    public void testGettersAndSetters() {
        OverallCreditValueCollection collection = new OverallCreditValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(values, collection.getValues());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        OverallCreditValueCollection collection1 = new OverallCreditValueCollection().setValues(values).setDeltas(deltas);
        OverallCreditValueCollection collection2 = new OverallCreditValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        OverallCreditValueCollection collection = new OverallCreditValueCollection().setValues(values).setDeltas(deltas);
        assertEquals(-259824333, collection.hashCode());
    }

    @Test
    public void testToString() {
        OverallCreditValueCollection collection = new OverallCreditValueCollection().setValues(values).setDeltas(deltas);
        assertEquals("OverallCreditValueCollection{values=" + values + ", deltas=" + deltas + "}",
                collection.toString());
    }
}
