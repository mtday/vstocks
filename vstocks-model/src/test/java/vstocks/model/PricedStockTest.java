package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class PricedStockTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testAsStock() {
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
    }

    @Test
    public void testEquals() {
        PricedStock pricedStock1 = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20);
        PricedStock pricedStock2 = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20);
        assertEquals(pricedStock1, pricedStock2);
    }

    @Test
    public void testHashCode() {
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(timestamp)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20);
        assertEquals(-547261559, pricedStock.hashCode());
    }

    @Test
    public void testToString() {
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20);
        assertEquals("PricedStock{market=Twitter, symbol='symbol', timestamp=" + now + ", name='name', "
                + "profileImage='link', price=20}", pricedStock.toString());
    }
}
