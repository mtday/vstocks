package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class PricedUserStockTest {
    @Test
    public void testAsUserStock() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock = new PricedUserStock()
                .setUserId("TW:12345")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setTimestamp(now)
                .setPrice(20)
                .asUserStock();

        assertEquals("TW:12345", userStock.getUserId());
        assertEquals(TWITTER, userStock.getMarket());
        assertEquals("symbol", userStock.getSymbol());
        assertEquals(10, userStock.getShares());
    }

    @Test
    public void testAsStockPrice() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice = new PricedUserStock()
                .setUserId("TW:12345")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
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
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId("TW:12345")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setTimestamp(now)
                .setPrice(20);

        assertEquals("TW:12345", pricedUserStock.getUserId());
        assertEquals(TWITTER, pricedUserStock.getMarket());
        assertEquals("symbol", pricedUserStock.getSymbol());
        assertEquals(10, pricedUserStock.getShares());
        assertEquals(now, pricedUserStock.getTimestamp());
        assertEquals(20, pricedUserStock.getPrice());
    }

    @Test
    public void testEquals() {
        PricedUserStock pricedUserStock1 = new PricedUserStock().setUserId("user").setMarket(TWITTER).setSymbol("sym").setPrice(10);
        PricedUserStock pricedUserStock2 = new PricedUserStock().setUserId("user").setMarket(TWITTER).setSymbol("sym").setPrice(20);
        assertEquals(pricedUserStock1, pricedUserStock2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId("TW:12345")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setTimestamp(now)
                .setPrice(20);
        assertEquals(-381862016, pricedUserStock.hashCode());
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId("TW:12345")
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setTimestamp(now)
                .setPrice(20);
        assertEquals("PricedUserStock{userId='TW:12345', market=TWITTER, symbol='symbol', shares=10, timestamp="
                + now + ", price=20}", pricedUserStock.toString());
    }
}
