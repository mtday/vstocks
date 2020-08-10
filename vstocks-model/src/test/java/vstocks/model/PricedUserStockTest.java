package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class PricedUserStockTest {
    @Test
    public void testAsUserStock() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setTimestamp(now)
                .setPrice(20)
                .asUserStock();

        assertEquals(userId, userStock.getUserId());
        assertEquals(TWITTER, userStock.getMarket());
        assertEquals("symbol", userStock.getSymbol());
        assertEquals(10, userStock.getShares());
    }

    @Test
    public void testAsStockPrice() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice = new PricedUserStock()
                .setUserId(userId)
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
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setTimestamp(now)
                .setPrice(20);

        assertEquals(userId, pricedUserStock.getUserId());
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
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setTimestamp(now)
                .setPrice(20);
        assertEquals(1638071562, pricedUserStock.hashCode());
    }

    @Test
    public void testToString() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(userId)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setShares(10)
                .setTimestamp(now)
                .setPrice(20);
        assertEquals("PricedUserStock{userId='" + userId + "', market=TWITTER, symbol='symbol', shares=10, timestamp="
                + now + ", price=20}", pricedUserStock.toString());
    }
}
