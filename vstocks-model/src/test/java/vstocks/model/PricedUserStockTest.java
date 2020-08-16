package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class PricedUserStockTest {
    @Test
    public void testAsUserStock() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserStock userStock = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20)
                .asUserStock();

        assertEquals(userId, userStock.getUserId());
        assertEquals(TWITTER, userStock.getMarket());
        assertEquals("symbol", userStock.getSymbol());
        assertEquals(10, userStock.getShares());
    }

    @Test
    public void testAsStockPrice() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20)
                .asStockPrice();

        assertEquals(TWITTER, stockPrice.getMarket());
        assertEquals("symbol", stockPrice.getSymbol());
        assertEquals(now, stockPrice.getTimestamp());
        assertEquals(20, stockPrice.getPrice());
    }

    @Test
    public void testGettersAndSetters() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20);

        assertEquals(userId, pricedUserStock.getUserId());
        assertEquals(TWITTER, pricedUserStock.getMarket());
        assertEquals("symbol", pricedUserStock.getSymbol());
        assertEquals(now, pricedUserStock.getTimestamp());
        assertEquals(10, pricedUserStock.getShares());
        assertEquals(20, pricedUserStock.getPrice());

        assertEquals(0, PricedUserStock.FULL_COMPARATOR.compare(pricedUserStock, pricedUserStock));
        assertEquals(0, PricedUserStock.UNIQUE_COMPARATOR.compare(pricedUserStock, pricedUserStock));
    }

    @Test
    public void testEquals() {
        PricedUserStock pricedUserStock1 = new PricedUserStock().setUserId("user").setMarket(TWITTER).setSymbol("sym").setPrice(10);
        PricedUserStock pricedUserStock2 = new PricedUserStock().setUserId("user").setMarket(TWITTER).setSymbol("sym").setPrice(20);
        assertEquals(pricedUserStock1, pricedUserStock2);
    }

    @Test
    public void testHashCode() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.parse("2007-12-03T10:15:30.00Z");
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20);
        assertEquals(437287800, pricedUserStock.hashCode());
    }

    @Test
    public void testToString() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20);
        assertEquals("PricedUserStock{userId='" + userId + "', market=TWITTER, symbol='symbol', timestamp="
                + now + ", shares=10, price=20}", pricedUserStock.toString());
    }
}
