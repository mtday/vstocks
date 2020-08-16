package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class PricedStockTest {
    @Test
    public void testAsStock() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        Stock stock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20)
                .asStock();

        assertEquals(TWITTER, stock.getMarket());
        assertEquals("symbol", stock.getSymbol());
        assertEquals("name", stock.getName());
        assertEquals("link", stock.getProfileImage());
    }

    @Test
    public void testAsStockPrice() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20)
                .asStockPrice();

        assertEquals(TWITTER, stockPrice.getMarket());
        assertEquals("symbol", stockPrice.getSymbol());
        assertEquals(now, stockPrice.getTimestamp());
        assertEquals(20, stockPrice.getPrice());
    }

    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20);

        assertEquals(TWITTER, pricedStock.getMarket());
        assertEquals("symbol", pricedStock.getSymbol());
        assertEquals("name", pricedStock.getName());
        assertEquals("link", pricedStock.getProfileImage());
        assertEquals(now, pricedStock.getTimestamp());
        assertEquals(20, pricedStock.getPrice());

        assertEquals(0, PricedStock.FULL_COMPARATOR.compare(pricedStock, pricedStock));
        assertEquals(0, PricedStock.UNIQUE_COMPARATOR.compare(pricedStock, pricedStock));
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedStock pricedStock1 = new PricedStock().setMarket(TWITTER).setSymbol("sym").setTimestamp(now).setName("name1");
        PricedStock pricedStock2 = new PricedStock().setMarket(TWITTER).setSymbol("sym").setTimestamp(now).setName("name2");
        assertEquals(pricedStock1, pricedStock2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20);
        assertEquals(2099410588, pricedStock.hashCode());
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20);
        assertEquals("PricedStock{market=TWITTER, symbol='symbol', timestamp=" + now.toString()
                + ", name='name', profileImage='link', price=20}", pricedStock.toString());
    }
}
