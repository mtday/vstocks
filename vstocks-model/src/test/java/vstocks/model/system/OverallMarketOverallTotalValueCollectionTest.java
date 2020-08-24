package vstocks.model.system;

import org.junit.Test;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class OverallMarketOverallTotalValueCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final OverallMarketTotalValue overallMarketTotalValue1 = new OverallMarketTotalValue()
            .setTimestamp(timestamp)
            .setValue(20);
    private final OverallMarketTotalValue overallMarketTotalValue2 = new OverallMarketTotalValue()
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);

    private final List<OverallMarketTotalValue> values = asList(overallMarketTotalValue1, overallMarketTotalValue2);
    private final Map<DeltaInterval, Delta> deltas =
            Delta.getDeltas(values, OverallMarketTotalValue::getTimestamp, OverallMarketTotalValue::getValue);

    @Test
    public void testGettersAndSetters() {
        OverallMarketTotalValueCollection collection = new OverallMarketTotalValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(values, collection.getValues());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        OverallMarketTotalValueCollection collection1 = new OverallMarketTotalValueCollection().setValues(values).setDeltas(deltas);
        OverallMarketTotalValueCollection collection2 = new OverallMarketTotalValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        OverallMarketTotalValueCollection collection = new OverallMarketTotalValueCollection().setValues(values).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        OverallMarketTotalValueCollection collection = new OverallMarketTotalValueCollection().setValues(values).setDeltas(deltas);
        assertEquals("OverallMarketTotalValueCollection{values=" + values + ", deltas=" + deltas + "}",
                collection.toString());
    }
}
