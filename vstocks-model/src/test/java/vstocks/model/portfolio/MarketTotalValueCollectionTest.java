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

public class MarketTotalValueCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final MarketTotalValue marketTotalValue1 = new MarketTotalValue()
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setValue(20);
    private final MarketTotalValue marketTotalValue2 = new MarketTotalValue()
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);

    private final List<MarketTotalValue> values = asList(marketTotalValue1, marketTotalValue2);
    private final Map<DeltaInterval, Delta> deltas =
            Delta.getDeltas(values, MarketTotalValue::getTimestamp, MarketTotalValue::getValue);

    @Test
    public void testGettersAndSetters() {
        MarketTotalValueCollection collection = new MarketTotalValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(values, collection.getValues());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        MarketTotalValueCollection collection1 = new MarketTotalValueCollection().setValues(values).setDeltas(deltas);
        MarketTotalValueCollection collection2 = new MarketTotalValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        MarketTotalValueCollection collection = new MarketTotalValueCollection().setValues(values).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        MarketTotalValueCollection collection = new MarketTotalValueCollection().setValues(values).setDeltas(deltas);
        assertEquals("MarketTotalValueCollection{values=" + values + ", deltas=" + deltas + "}", collection.toString());
    }
}
