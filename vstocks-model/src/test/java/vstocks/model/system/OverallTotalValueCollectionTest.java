package vstocks.model.system;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Delta.getDeltas;

public class OverallTotalValueCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final OverallTotalValue overallTotalValue1 = new OverallTotalValue().setTimestamp(timestamp).setValue(20);
    private final OverallTotalValue overallTotalValue2 = new OverallTotalValue()
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);

    private final List<OverallTotalValue> values = asList(overallTotalValue1, overallTotalValue2);
    private final List<Delta> deltas = getDeltas(values, OverallTotalValue::getTimestamp, OverallTotalValue::getValue);

    @Test
    public void testGettersAndSetters() {
        OverallTotalValueCollection collection = new OverallTotalValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(values, collection.getValues());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        OverallTotalValueCollection collection1 = new OverallTotalValueCollection().setValues(values).setDeltas(deltas);
        OverallTotalValueCollection collection2 = new OverallTotalValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        OverallTotalValueCollection collection = new OverallTotalValueCollection().setValues(values).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        OverallTotalValueCollection collection = new OverallTotalValueCollection().setValues(values).setDeltas(deltas);
        assertEquals("OverallTotalValueCollection{values=" + values + ", deltas=" + deltas + "}", collection.toString());
    }
}
