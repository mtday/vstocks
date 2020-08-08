package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.Market.TWITTER;

public class StockTest {
    @Test
    public void testGettersAndSetters() {
        Stock stock = new Stock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setActive(true);

        assertEquals(TWITTER, stock.getMarket());
        assertEquals("symbol", stock.getSymbol());
        assertEquals("name", stock.getName());
        assertTrue(stock.isActive());
    }

    @Test
    public void testEquals() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name2");
        assertEquals(stock1, stock2);
    }

    @Test
    public void testHashCode() {
        Stock stock = new Stock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setActive(true);
        assertEquals(1553141094, stock.hashCode());
    }

    @Test
    public void testToString() {
        Stock stock = new Stock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setActive(true);
        assertEquals("Stock{market=TWITTER, symbol='symbol', name='name', active=true}", stock.toString());
    }
}
