package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class StockPriceChangeTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        StockPriceChange stockPriceChange = new StockPriceChange()
                .setBatch(1)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20)
                .setChange(2)
                .setPercent(2.5f);

        assertEquals(TWITTER, stockPriceChange.getMarket());
        assertEquals("symbol", stockPriceChange.getSymbol());
        assertEquals(now, stockPriceChange.getTimestamp());
        assertEquals(20, stockPriceChange.getPrice());
        assertEquals(2, stockPriceChange.getChange());
        assertEquals(2.5f, stockPriceChange.getPercent(), 0.001);
    }

    @Test
    public void testEquals() {
        StockPriceChange stockPriceChange1 = new StockPriceChange()
                .setBatch(1)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20)
                .setChange(2)
                .setPercent(2.5f);
        StockPriceChange stockPriceChange2 = new StockPriceChange()
                .setBatch(1)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20)
                .setChange(2)
                .setPercent(2.5f);
        assertEquals(stockPriceChange1, stockPriceChange2);
    }

    @Test
    public void testHashCode() {
        StockPriceChange stockPriceChange = new StockPriceChange()
                .setBatch(1)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(timestamp)
                .setPrice(20)
                .setChange(2)
                .setPercent(2.5f);
        assertEquals(-1073702763, stockPriceChange.hashCode());
    }

    @Test
    public void testToString() {
        StockPriceChange stockPriceChange = new StockPriceChange()
                .setBatch(1)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20)
                .setChange(2)
                .setPercent(2.5f);
        assertEquals("StockPriceChange{batch=1, market=Twitter, symbol='symbol', timestamp=" + now
                        + ", price=20, change=2, percent=2.5}", stockPriceChange.toString());
    }
}
