package vstocks.model.system;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Delta.getDeltas;
import static vstocks.model.Market.TWITTER;

public class OverallMarketValueCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final OverallMarketValue overallMarketValue1 = new OverallMarketValue()
            .setMarket(TWITTER)
            .setTimestamp(timestamp)
            .setValue(20);
    private final OverallMarketValue overallMarketValue2 = new OverallMarketValue()
            .setMarket(TWITTER)
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);

    private final List<OverallMarketValue> values = asList(overallMarketValue1, overallMarketValue2);
    private final List<Delta> deltas = getDeltas(values, OverallMarketValue::getTimestamp, OverallMarketValue::getValue);

    @Test
    public void testGettersAndSetters() {
        OverallMarketValueCollection collection =
                new OverallMarketValueCollection().setMarket(TWITTER).setValues(values).setDeltas(deltas);

        assertEquals(values, collection.getValues());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        OverallMarketValueCollection collection1 =
                new OverallMarketValueCollection().setMarket(TWITTER).setValues(values).setDeltas(deltas);
        OverallMarketValueCollection collection2 =
                new OverallMarketValueCollection().setMarket(TWITTER).setValues(values).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        OverallMarketValueCollection collection =
                new OverallMarketValueCollection().setMarket(TWITTER).setValues(values).setDeltas(deltas);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        OverallMarketValueCollection collection =
                new OverallMarketValueCollection().setMarket(TWITTER).setValues(values).setDeltas(deltas);
        assertEquals("OverallMarketValueCollection{market=Twitter, values=" + values + ", deltas=" + deltas + "}",
                collection.toString());
    }
}
