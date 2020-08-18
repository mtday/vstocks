package vstocks.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
    }

    @Test
    public void testEquals() {
        Stock stock1 = new Stock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("link");
        Stock stock2 = new Stock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("link");
        assertEquals(stock1, stock2);
    }

    @Test
    public void testHashCode() {
        Stock stock = new Stock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("link");
        assertEquals(923521, new Stock().hashCode());
        assertNotEquals(0, stock.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Stock stock = new Stock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("link");
        assertEquals("Stock{market=Twitter, symbol='symbol', name='name', profileImage='link'}", stock.toString());
    }
}
