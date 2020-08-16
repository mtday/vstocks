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
                .setName("name")
                .setProfileImage("link");

        assertEquals(TWITTER, stock.getMarket());
        assertEquals("symbol", stock.getSymbol());
        assertEquals("name", stock.getName());
        assertEquals("link", stock.getProfileImage());

        assertEquals(0, Stock.FULL_COMPARATOR.compare(stock, stock));
        assertEquals(0, Stock.UNIQUE_COMPARATOR.compare(stock, stock));
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
                .setProfileImage("link");
        assertEquals(1553141094, stock.hashCode());
    }

    @Test
    public void testToString() {
        Stock stock = new Stock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("link");
        assertEquals("Stock{market=TWITTER, symbol='symbol', name='name', profileImage='link'}", stock.toString());
    }
}
