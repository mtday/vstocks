package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;

public class StockPriceChangeCollectionTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final StockPriceChange stockPriceChange1 = new StockPriceChange()
            .setBatch(2)
            .setMarket(TWITTER)
            .setSymbol("symbol")
            .setTimestamp(timestamp)
            .setPrice(20)
            .setChange(2)
            .setPercent(2.5f);
    private final StockPriceChange stockPriceChange2 = new StockPriceChange()
            .setBatch(1)
            .setMarket(TWITTER)
            .setSymbol("symbol")
            .setTimestamp(timestamp.minusSeconds(10))
            .setPrice(20)
            .setChange(2)
            .setPercent(2.5f);

    private final List<StockPriceChange> changes = asList(stockPriceChange1, stockPriceChange2);

    @Test
    public void testGettersAndSetters() {
        StockPriceChangeCollection collection = new StockPriceChangeCollection().setChanges(changes);
        assertEquals(changes, collection.getChanges());
    }

    @Test
    public void testEquals() {
        StockPriceChangeCollection collection1 = new StockPriceChangeCollection().setChanges(changes);
        StockPriceChangeCollection collection2 = new StockPriceChangeCollection().setChanges(changes);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        StockPriceChangeCollection collection = new StockPriceChangeCollection().setChanges(changes);
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        StockPriceChangeCollection collection = new StockPriceChangeCollection().setChanges(changes);
        assertEquals("StockPriceChangeCollection{changes=" + changes + "}", collection.toString());
    }
}
