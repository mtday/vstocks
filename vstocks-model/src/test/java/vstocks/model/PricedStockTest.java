package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.*;
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
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )))
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
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )))
                .asStockPrice();

        assertEquals(TWITTER, stockPrice.getMarket());
        assertEquals("symbol", stockPrice.getSymbol());
        assertEquals(now, stockPrice.getTimestamp());
        assertEquals(20, stockPrice.getPrice());
    }

    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20)
                .setDeltas(deltas);

        assertEquals(TWITTER, pricedStock.getMarket());
        assertEquals("symbol", pricedStock.getSymbol());
        assertEquals("name", pricedStock.getName());
        assertEquals("link", pricedStock.getProfileImage());
        assertEquals(now, pricedStock.getTimestamp());
        assertEquals(20, pricedStock.getPrice());
        assertEquals(deltas, pricedStock.getDeltas());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedStock pricedStock1 = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        PricedStock pricedStock2 = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(now)
                .setName("name")
                .setProfileImage("link")
                .setPrice(20)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals(pricedStock1, pricedStock2);
    }

    @Test
    public void testHashCode() {
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(Instant.parse("2007-12-03T10:15:30.00Z"))
                .setName("name")
                .setProfileImage("link")
                .setPrice(20)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals(1742810335, new PricedStock().hashCode());
        assertNotEquals(0, pricedStock.hashCode()); // enums make the value inconsistent
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
                .setPrice(20)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals("PricedStock{market=TWITTER, symbol='symbol', timestamp=" + now.toString() + ", name='name', "
                + "profileImage='link', price=20, deltas={6h=Delta{interval=6h, change=5, percent=5.25}, "
                + "12h=Delta{interval=12h, change=5, percent=5.25}, 1d=Delta{interval=1d, change=10, percent=10.25}}}",
                pricedStock.toString());
    }
}
