package vstocks.model;

import org.junit.Test;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class PortfolioValueTest {
    @Test
    public void testGettersAndSetters() {
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId("TW:12345")
                .setCredits(1234L)
                .setMarketValues(singletonMap(TWITTER, 10L))
                .setTotal(1244L);

        assertEquals("TW:12345", portfolioValue.getUserId());
        assertEquals(1234, portfolioValue.getCredits());
        assertEquals(singletonMap(TWITTER, 10L), portfolioValue.getMarketValues());
        assertEquals(1244, portfolioValue.getTotal());
    }

    @Test
    public void testEquals() {
        PortfolioValue portfolioValue1 = new PortfolioValue().setUserId("user").setTotal(1234);
        PortfolioValue portfolioValue2 = new PortfolioValue().setUserId("user").setTotal(4321);
        assertEquals(portfolioValue1, portfolioValue2);
    }

    @Test
    public void testHashCode() {
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId("TW:12345")
                .setCredits(1234L)
                .setMarketValues(singletonMap(TWITTER, 10L))
                .setTotal(1244L);
        assertEquals(1977872539, portfolioValue.hashCode());
    }

    @Test
    public void testToString() {
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId("TW:12345")
                .setCredits(1234L)
                .setMarketValues(singletonMap(TWITTER, 10L))
                .setTotal(1244L);
        assertEquals("PortfolioValue{userId='TW:12345', credits=1234, marketValues={TWITTER=10}, total=1244}",
                portfolioValue.toString());
    }
}
