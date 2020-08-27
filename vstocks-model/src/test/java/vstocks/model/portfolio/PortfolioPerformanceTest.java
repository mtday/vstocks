package vstocks.model.portfolio;

import org.junit.Test;
import vstocks.model.Delta;

import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static vstocks.model.Delta.getDeltas;
import static vstocks.model.Market.TWITTER;

public class PortfolioPerformanceTest {
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    private final TotalRank totalRank1 = new TotalRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setRank(20);
    private final TotalRank totalRank2 = new TotalRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setRank(18);
    private final List<TotalRank> totalRanks = asList(totalRank1, totalRank2);
    private final List<Delta> totalRankDeltas = getDeltas(totalRanks, TotalRank::getTimestamp, TotalRank::getRank);
    private final TotalRankCollection totalRankCollection = new TotalRankCollection()
            .setRanks(totalRanks)
            .setDeltas(totalRankDeltas);

    private final CreditRank creditRank1 = new CreditRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setRank(20);
    private final CreditRank creditRank2 = new CreditRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setRank(18);
    private final List<CreditRank> creditRanks = asList(creditRank1, creditRank2);
    private final List<Delta> creditRankDeltas = getDeltas(creditRanks, CreditRank::getTimestamp, CreditRank::getRank);
    private final CreditRankCollection creditRankCollection = new CreditRankCollection()
            .setRanks(creditRanks)
            .setDeltas(creditRankDeltas);

    private final MarketTotalRank marketTotalRank1 = new MarketTotalRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setRank(20);
    private final MarketTotalRank marketTotalRank2 = new MarketTotalRank()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setRank(18);
    private final List<MarketTotalRank> marketTotalRanks = asList(marketTotalRank1, marketTotalRank2);
    private final List<Delta> marketTotalRankDeltas =
            getDeltas(marketTotalRanks, MarketTotalRank::getTimestamp, MarketTotalRank::getRank);
    private final MarketTotalRankCollection marketTotalRankCollection = new MarketTotalRankCollection()
            .setRanks(marketTotalRanks)
            .setDeltas(marketTotalRankDeltas);

    private final MarketRank marketRank1 = new MarketRank()
            .setBatch(1)
            .setUserId("userId")
            .setMarket(TWITTER)
            .setTimestamp(timestamp)
            .setRank(20);
    private final MarketRank marketRank2 = new MarketRank()
            .setBatch(1)
            .setUserId("userId")
            .setMarket(TWITTER)
            .setTimestamp(timestamp.minusSeconds(10))
            .setRank(18);
    private final List<MarketRank> marketRanks = asList(marketRank1, marketRank2);
    private final List<Delta> marketRankDeltas = getDeltas(marketRanks, MarketRank::getTimestamp, MarketRank::getRank);
    private final MarketRankCollection marketRankCollection = new MarketRankCollection()
            .setMarket(TWITTER)
            .setRanks(marketRanks)
            .setDeltas(marketRankDeltas);
    private final List<MarketRankCollection> marketRankMap = singletonList(marketRankCollection);

    @Test
    public void testGettersAndSetters() {
        PortfolioPerformance portfolioPerformance = new PortfolioPerformance()
                .setTotalRanks(totalRankCollection)
                .setCreditRanks(creditRankCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketRanks(marketRankMap);

        assertEquals(totalRankCollection, portfolioPerformance.getTotalRanks());
        assertEquals(creditRankCollection, portfolioPerformance.getCreditRanks());
        assertEquals(marketTotalRankCollection, portfolioPerformance.getMarketTotalRanks());
        assertEquals(marketRankMap, portfolioPerformance.getMarketRanks());
    }

    @Test
    public void testEquals() {
        PortfolioPerformance portfolioPerformance1 = new PortfolioPerformance()
                .setTotalRanks(totalRankCollection)
                .setCreditRanks(creditRankCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketRanks(marketRankMap);
        PortfolioPerformance portfolioPerformance2 = new PortfolioPerformance()
                .setTotalRanks(totalRankCollection)
                .setCreditRanks(creditRankCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketRanks(marketRankMap);
        assertEquals(portfolioPerformance1, portfolioPerformance2);
    }

    @Test
    public void testHashCode() {
        PortfolioPerformance portfolioPerformance = new PortfolioPerformance()
                .setTotalRanks(totalRankCollection)
                .setCreditRanks(creditRankCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketRanks(marketRankMap);
        assertEquals(923521, new PortfolioPerformance().hashCode());
        assertNotEquals(0, portfolioPerformance.hashCode()); // enums cause inconsistent values
    }

    @Test
    public void testToString() {
        PortfolioPerformance portfolioPerformance = new PortfolioPerformance()
                .setTotalRanks(totalRankCollection)
                .setCreditRanks(creditRankCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketRanks(marketRankMap);
        assertNotNull(portfolioPerformance.toString()); // skipping real check
    }
}
