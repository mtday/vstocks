package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;

public class StockPriceTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
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
        Instant now = Instant.now().truncatedTo(SECONDS);
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
        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");
        StockPrice stockPrice = new StockPrice()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20);
        assertEquals(923521, new StockPrice().hashCode());
        assertNotEquals(0, stockPrice.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice = new StockPrice()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20);
        assertEquals("StockPrice{market=TWITTER, symbol='symbol', "
                + "timestamp=" + now.toString() + ", price=20}", stockPrice.toString());
    }
}
