package vstocks.model.portfolio;

import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class PortfolioValueTest {
    private final PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary();
    private final CreditRankCollection creditRankCollection = new CreditRankCollection();
    private final MarketTotalRankCollection marketTotalRankCollection = new MarketTotalRankCollection();
    private final List<MarketRankCollection> marketRankCollections =
            singletonList(new MarketRankCollection().setMarket(TWITTER));
    private final TotalRankCollection totalRankCollection = new TotalRankCollection();

    @Test
    public void testGettersAndSetters() {
        PortfolioValue portfolioValue = new PortfolioValue()
                .setSummary(portfolioValueSummary)
                .setCreditRanks(creditRankCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketRanks(marketRankCollections)
                .setTotalRanks(totalRankCollection);

        assertEquals(portfolioValueSummary, portfolioValue.getSummary());
        assertEquals(creditRankCollection, portfolioValue.getCreditRanks());
        assertEquals(marketTotalRankCollection, portfolioValue.getMarketTotalRanks());
        assertEquals(marketRankCollections, portfolioValue.getMarketRanks());
        assertEquals(totalRankCollection, portfolioValue.getTotalRanks());
    }

    @Test
    public void testEquals() {
        PortfolioValue portfolioValue1 = new PortfolioValue()
                .setSummary(portfolioValueSummary)
                .setCreditRanks(creditRankCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketRanks(marketRankCollections)
                .setTotalRanks(totalRankCollection);
        PortfolioValue portfolioValue2 = new PortfolioValue()
                .setSummary(portfolioValueSummary)
                .setCreditRanks(creditRankCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketRanks(marketRankCollections)
                .setTotalRanks(totalRankCollection);
        assertEquals(portfolioValue1, portfolioValue2);
    }

    @Test
    public void testHashCode() {
        PortfolioValue portfolioValue = new PortfolioValue()
                .setSummary(portfolioValueSummary)
                .setCreditRanks(creditRankCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketRanks(marketRankCollections)
                .setTotalRanks(totalRankCollection);
        assertEquals(288628142, portfolioValue.hashCode());
    }

    @Test
    public void testToString() {
        PortfolioValue portfolioValue = new PortfolioValue()
                .setSummary(portfolioValueSummary)
                .setCreditRanks(creditRankCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketRanks(marketRankCollections)
                .setTotalRanks(totalRankCollection);
        assertEquals("PortfolioValue{summary=" + portfolioValueSummary + ", creditRanks=" + creditRankCollection
                + ", marketTotalRanks=" + marketTotalRankCollection + ", marketRanks=" + marketRankCollections
                + ", totalRanks=" + totalRankCollection + "}", portfolioValue.toString());
    }
}
