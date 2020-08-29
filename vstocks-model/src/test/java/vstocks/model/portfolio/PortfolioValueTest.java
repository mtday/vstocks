package vstocks.model.portfolio;

import org.junit.Test;
import vstocks.model.Market;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.User.generateId;

public class PortfolioValueTest {
    @Test
    public void testGettersAndSetters() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, (long) market.ordinal()));
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId("userId")
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);

        assertEquals("userId", portfolioValue.getUserId());
        assertEquals(1, portfolioValue.getCredits());
        assertEquals(2, portfolioValue.getMarketTotal());
        assertEquals(marketValues, portfolioValue.getMarketValues());
        assertEquals(3, portfolioValue.getTotal());
    }

    @Test
    public void testEquals() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, (long) market.ordinal()));
        PortfolioValue portfolioValue1 = new PortfolioValue()
                .setUserId(generateId("user@domain.com"))
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);
        PortfolioValue portfolioValue2 = new PortfolioValue()
                .setUserId(generateId("user@domain.com"))
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);
        assertEquals(portfolioValue1, portfolioValue2);
    }

    @Test
    public void testHashCode() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, (long) market.ordinal()));
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(generateId("user@domain.com"))
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);
        assertEquals(28629151, new PortfolioValue().hashCode());
        assertNotEquals(0, portfolioValue.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, (long) market.ordinal()));
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(generateId("user@domain.com"))
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);
        assertEquals("PortfolioValue{userId='cd2bfcff-e5fe-34a1-949d-101994d0987f', credits=1, "
                        + "marketTotal=2, marketValues={Twitter=0, YouTube=1, Instagram=2, Twitch=3, "
                        + "Facebook=4}, total=3}", portfolioValue.toString());
    }
}
