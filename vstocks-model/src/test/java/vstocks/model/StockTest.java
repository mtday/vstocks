package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class StockTest {
    @Test
    public void testGettersAndSetters() {
        Stock stock = new Stock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name");

        assertEquals(TWITTER, stock.getMarket());
        assertEquals("symbol", stock.getSymbol());
        assertEquals("name", stock.getName());
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
                .setName("name");
        assertEquals(1553141094, stock.hashCode());
    }

    @Test
    public void testToString() {
        Stock stock = new Stock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name");
        assertEquals("Stock{market=TWITTER, symbol='symbol', name='name', active=true}", stock.toString());
    }
}
