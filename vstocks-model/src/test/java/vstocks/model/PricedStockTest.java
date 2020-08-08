package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.Market.TWITTER;

public class PricedStockTest {
    @Test
    public void testAsStock() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setActive(true)
                .setTimestamp(now)
                .setPrice(20)
                .asStock();

        assertEquals(TWITTER, stock.getMarket());
        assertEquals("symbol", stock.getSymbol());
        assertEquals("name", stock.getName());
        assertTrue(stock.isActive());
    }

    @Test
    public void testAsStockPrice() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setActive(true)
                .setTimestamp(now)
                .setPrice(20)
                .asStockPrice();

        assertEquals(TWITTER, stockPrice.getMarket());
        assertEquals("symbol", stockPrice.getSymbol());
        assertEquals(now, stockPrice.getTimestamp());
        assertEquals(20, stockPrice.getPrice());
    }

    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setActive(true)
                .setTimestamp(now)
                .setPrice(20);

        assertEquals(TWITTER, pricedStock.getMarket());
        assertEquals("symbol", pricedStock.getSymbol());
        assertEquals("name", pricedStock.getName());
        assertTrue(pricedStock.isActive());
        assertEquals(now, pricedStock.getTimestamp());
        assertEquals(20, pricedStock.getPrice());
    }

    @Test
    public void testEquals() {
        PricedStock pricedStock1 = new PricedStock().setMarket(TWITTER).setSymbol("sym").setName("name1");
        PricedStock pricedStock2 = new PricedStock().setMarket(TWITTER).setSymbol("sym").setName("name2");
        assertEquals(pricedStock1, pricedStock2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setActive(true)
                .setTimestamp(now)
                .setPrice(20);
        assertEquals(1553141094, pricedStock.hashCode());
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setActive(true)
                .setTimestamp(now)
                .setPrice(20);
        assertEquals("PricedStock{market=TWITTER, symbol='symbol', name='name', active=true, "
                + "timestamp=" + now.toString() + ", price=20}", pricedStock.toString());
    }
}
