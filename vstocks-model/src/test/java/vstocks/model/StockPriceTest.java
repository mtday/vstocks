package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class StockPriceTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
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
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol("sym").setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol("sym").setPrice(20);
        assertEquals(stockPrice1, stockPrice2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice = new StockPrice()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20);
        assertEquals(1553141094, stockPrice.hashCode());
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice = new StockPrice()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setPrice(20);
        assertEquals("StockPrice{market=TWITTER, symbol='symbol', "
                + "timestamp=" + now.toString() + ", price=20}", stockPrice.toString());
    }
}
