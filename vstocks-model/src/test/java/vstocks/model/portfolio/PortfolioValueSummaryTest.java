package vstocks.model.portfolio;

import org.junit.Test;
import vstocks.model.Market;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.User.generateId;

public class PortfolioValueSummaryTest {
    @Test
    public void testGettersAndSetters() {
        List<MarketValue> marketValues = Arrays.stream(Market.values())
                .map(market -> new MarketValue().setMarket(market).setValue(market.ordinal()))
                .collect(toList());
        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary()
                .setUserId("userId")
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);

        assertEquals("userId", portfolioValueSummary.getUserId());
        assertEquals(1, portfolioValueSummary.getCredits());
        assertEquals(2, portfolioValueSummary.getMarketTotal());
        assertEquals(marketValues, portfolioValueSummary.getMarketValues());
        assertEquals(3, portfolioValueSummary.getTotal());
    }

    @Test
    public void testEquals() {
        List<MarketValue> marketValues = Arrays.stream(Market.values())
                .map(market -> new MarketValue().setMarket(market).setValue(market.ordinal()))
                .collect(toList());
        PortfolioValueSummary portfolioValueSummary1 = new PortfolioValueSummary()
                .setUserId(generateId("user@domain.com"))
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);
        PortfolioValueSummary portfolioValueSummary2 = new PortfolioValueSummary()
                .setUserId(generateId("user@domain.com"))
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);
        assertEquals(portfolioValueSummary1, portfolioValueSummary2);
    }

    @Test
    public void testHashCode() {
        List<MarketValue> marketValues = Arrays.stream(Market.values())
                .map(market -> new MarketValue().setMarket(market).setValue(market.ordinal()))
                .collect(toList());
        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary()
                .setUserId(generateId("user@domain.com"))
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);
        assertEquals(2077086716, portfolioValueSummary.hashCode());
    }

    @Test
    public void testToString() {
        List<MarketValue> marketValues = Arrays.stream(Market.values())
                .map(market -> new MarketValue().setMarket(market).setValue(market.ordinal()))
                .collect(toList());
        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary()
                .setUserId(generateId("user@domain.com"))
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);
        assertEquals("PortfolioValueSummary{userId='cd2bfcff-e5fe-34a1-949d-101994d0987f', credits=1, marketTotal=2, "
                + "marketValues=[MarketValue{market=Twitter, value=0}, MarketValue{market=YouTube, value=1}, "
                + "MarketValue{market=Instagram, value=2}, MarketValue{market=Twitch, value=3}, "
                + "MarketValue{market=Facebook, value=4}], total=3}", portfolioValueSummary.toString());
    }
}
