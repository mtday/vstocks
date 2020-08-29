package vstocks.model.market;

import org.junit.Test;
import vstocks.model.portfolio.MarketValue;

import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class MarketValueTest {
    @Test
    public void testGettersAndSetters() {
        MarketValue marketValue = new MarketValue().setMarket(TWITTER).setValue(10);

        assertEquals(TWITTER, marketValue.getMarket());
        assertEquals(10, marketValue.getValue());
    }

    @Test
    public void testEquals() {
        MarketValue marketValue1 = new MarketValue().setMarket(TWITTER).setValue(10);
        MarketValue marketValue2 = new MarketValue().setMarket(TWITTER).setValue(10);
        assertEquals(marketValue1, marketValue2);
    }

    @Test
    public void testHashCode() {
        MarketValue marketValue = new MarketValue().setMarket(TWITTER).setValue(10);
        assertEquals(0, marketValue.hashCode());
    }

    @Test
    public void testToString() {
        MarketValue marketValue = new MarketValue().setMarket(TWITTER).setValue(10);
        assertEquals("MarketValue{market=Twitter, value=10}", marketValue.toString());
    }
}
