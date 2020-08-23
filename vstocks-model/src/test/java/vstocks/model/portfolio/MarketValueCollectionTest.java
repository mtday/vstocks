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
import static vstocks.model.Market.TWITTER;

public class MarketValueCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final MarketValue marketValue1 = new MarketValue()
            .setUserId("userId")
            .setMarket(TWITTER)
            .setTimestamp(timestamp)
            .setValue(20);
    private final MarketValue marketValue2 = new MarketValue()
            .setUserId("userId")
            .setMarket(TWITTER)
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);

    private final List<MarketValue> values = asList(marketValue1, marketValue2);
    private final Map<DeltaInterval, Delta> deltas =
            Delta.getDeltas(values, MarketValue::getTimestamp, MarketValue::getValue);

    @Test
    public void testGettersAndSetters() {
        MarketValueCollection collection = new MarketValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(values, collection.getValues());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        MarketValueCollection collection1 = new MarketValueCollection().setValues(values).setDeltas(deltas);
        MarketValueCollection collection2 = new MarketValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        MarketValueCollection collection = new MarketValueCollection().setValues(values).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        MarketValueCollection collection = new MarketValueCollection().setValues(values).setDeltas(deltas);
        assertEquals("MarketValueCollection{values=" + values + ", deltas=" + deltas + "}", collection.toString());
    }
}
