package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class StockPriceTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        StockPrice stockPrice = new StockPrice()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20);

        assertEquals(TWITTER, stockPrice.getMarket());
        assertEquals("symbol", stockPrice.getSymbol());
        assertEquals(now, stockPrice.getTimestamp());
        assertEquals(20, stockPrice.getPrice());
    }

    @Test
    public void testEquals() {
        StockPrice stockPrice1 = new StockPrice()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20);
        StockPrice stockPrice2 = new StockPrice()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20);
        assertEquals(stockPrice1, stockPrice2);
    }

    @Test
    public void testHashCode() {
        StockPrice stockPrice = new StockPrice()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(timestamp)
                .setPrice(20);
        assertEquals(492038520, stockPrice.hashCode());
    }

    @Test
    public void testToString() {
        StockPrice stockPrice = new StockPrice()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20);
        assertEquals("StockPrice{market=Twitter, symbol='symbol', timestamp=" + now + ", price=20}",
                stockPrice.toString());
    }
}
