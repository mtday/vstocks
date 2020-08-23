package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.User.generateId;

public class PricedUserStockTest {
    private final String userId = generateId("user@domain.com");
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testAsUserStock() {
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
    }

    @Test
    public void testEquals() {
        PricedUserStock pricedUserStock1 = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20);
        PricedUserStock pricedUserStock2 = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20);
        assertEquals(pricedUserStock1, pricedUserStock2);
    }

    @Test
    public void testHashCode() {
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(timestamp)
                .setShares(10)
                .setPrice(20);
        assertEquals(887503681, new PricedUserStock().hashCode());
        assertNotEquals(0, pricedUserStock.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setShares(10)
                .setPrice(20);
        assertEquals("PricedUserStock{userId='" + userId + "', market=Twitter, symbol='symbol', timestamp=" + now
                + ", shares=10, price=20}", pricedUserStock.toString());
    }
}
