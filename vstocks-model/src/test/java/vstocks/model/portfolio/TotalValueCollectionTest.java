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

public class TotalValueCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final TotalValue totalValue1 = new TotalValue()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setValue(20);
    private final TotalValue totalValue2 = new TotalValue()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);

    private final List<TotalValue> values = asList(totalValue1, totalValue2);
    private final Map<DeltaInterval, Delta> deltas =
            Delta.getDeltas(values, TotalValue::getTimestamp, TotalValue::getValue);

    @Test
    public void testGettersAndSetters() {
        TotalValueCollection collection = new TotalValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(values, collection.getValues());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        TotalValueCollection collection1 = new TotalValueCollection().setValues(values).setDeltas(deltas);
        TotalValueCollection collection2 = new TotalValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        TotalValueCollection collection = new TotalValueCollection().setValues(values).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        TotalValueCollection collection = new TotalValueCollection().setValues(values).setDeltas(deltas);
        assertEquals("TotalValueCollection{values=" + values + ", deltas=" + deltas + "}", collection.toString());
    }
}
