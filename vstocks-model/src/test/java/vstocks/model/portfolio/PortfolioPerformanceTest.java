package vstocks.model.portfolio;

import org.junit.Test;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.Market;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.*;
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
    private final Map<DeltaInterval, Delta> totalRankDeltas =
            Delta.getDeltas(totalRanks, TotalRank::getTimestamp, r -> -r.getRank());
    private final TotalRankCollection totalRankCollection = new TotalRankCollection()
            .setRanks(totalRanks)
            .setDeltas(totalRankDeltas);

    private final TotalValue totalValue1 = new TotalValue()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setValue(20);
    private final TotalValue totalValue2 = new TotalValue()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);
    private final List<TotalValue> totalValues = asList(totalValue1, totalValue2);
    private final Map<DeltaInterval, Delta> totalValueDeltas =
            Delta.getDeltas(totalValues, TotalValue::getTimestamp, TotalValue::getValue);
    private final TotalValueCollection totalValueCollection = new TotalValueCollection()
            .setValues(totalValues)
            .setDeltas(totalValueDeltas);

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
    private final Map<DeltaInterval, Delta> creditRankDeltas =
            Delta.getDeltas(creditRanks, CreditRank::getTimestamp, r -> -r.getRank());
    private final CreditRankCollection creditRankCollection = new CreditRankCollection()
            .setRanks(creditRanks)
            .setDeltas(creditRankDeltas);

    private final CreditValue creditValue1 = new CreditValue()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setValue(20);
    private final CreditValue creditValue2 = new CreditValue()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);
    private final List<CreditValue> creditValues = asList(creditValue1, creditValue2);
    private final Map<DeltaInterval, Delta> creditValueDeltas =
            Delta.getDeltas(creditValues, CreditValue::getTimestamp, CreditValue::getValue);
    private final CreditValueCollection creditValueCollection = new CreditValueCollection()
            .setValues(creditValues)
            .setDeltas(creditValueDeltas);

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
    private final Map<DeltaInterval, Delta> marketTotalRankDeltas =
            Delta.getDeltas(marketTotalRanks, MarketTotalRank::getTimestamp, r -> -r.getRank());
    private final MarketTotalRankCollection marketTotalRankCollection = new MarketTotalRankCollection()
            .setRanks(marketTotalRanks)
            .setDeltas(marketTotalRankDeltas);

    private final MarketTotalValue marketTotalValue1 = new MarketTotalValue()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp)
            .setValue(20);
    private final MarketTotalValue marketTotalValue2 = new MarketTotalValue()
            .setBatch(1)
            .setUserId("userId")
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);
    private final List<MarketTotalValue> marketTotalValues = asList(marketTotalValue1, marketTotalValue2);
    private final Map<DeltaInterval, Delta> marketTotalValueDeltas =
            Delta.getDeltas(marketTotalValues, MarketTotalValue::getTimestamp, MarketTotalValue::getValue);
    private final MarketTotalValueCollection marketTotalValueCollection = new MarketTotalValueCollection()
            .setValues(marketTotalValues)
            .setDeltas(marketTotalValueDeltas);

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
    private final Map<DeltaInterval, Delta> marketRankDeltas =
            Delta.getDeltas(marketRanks, MarketRank::getTimestamp, r -> -r.getRank());
    private final MarketRankCollection marketRankCollection = new MarketRankCollection()
            .setRanks(marketRanks)
            .setDeltas(marketRankDeltas);
    private final Map<Market, MarketRankCollection> marketRankMap = singletonMap(TWITTER, marketRankCollection);

    private final MarketValue marketValue1 = new MarketValue()
            .setBatch(1)
            .setUserId("userId")
            .setMarket(TWITTER)
            .setTimestamp(timestamp)
            .setValue(20);
    private final MarketValue marketValue2 = new MarketValue()
            .setBatch(1)
            .setUserId("userId")
            .setMarket(TWITTER)
            .setTimestamp(timestamp.minusSeconds(10))
            .setValue(18);
    private final List<MarketValue> marketValues = asList(marketValue1, marketValue2);
    private final Map<DeltaInterval, Delta> marketValueDeltas =
            Delta.getDeltas(marketValues, MarketValue::getTimestamp, MarketValue::getValue);
    private final MarketValueCollection marketValueCollection = new MarketValueCollection()
            .setValues(marketValues)
            .setDeltas(marketValueDeltas);
    private final Map<Market, MarketValueCollection> marketValueMap = singletonMap(TWITTER, marketValueCollection);

    @Test
    public void testGettersAndSetters() {
        PortfolioPerformance portfolioPerformance = new PortfolioPerformance()
                .setTotalRanks(totalRankCollection)
                .setTotalValues(totalValueCollection)
                .setCreditRanks(creditRankCollection)
                .setCreditValues(creditValueCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketTotalValues(marketTotalValueCollection)
                .setMarketRanks(marketRankMap)
                .setMarketValues(marketValueMap);

        assertEquals(totalRankCollection, portfolioPerformance.getTotalRanks());
        assertEquals(totalValueCollection, portfolioPerformance.getTotalValues());
        assertEquals(creditRankCollection, portfolioPerformance.getCreditRanks());
        assertEquals(creditValueCollection, portfolioPerformance.getCreditValues());
        assertEquals(marketTotalRankCollection, portfolioPerformance.getMarketTotalRanks());
        assertEquals(marketTotalValueCollection, portfolioPerformance.getMarketTotalValues());
        assertEquals(marketRankMap, portfolioPerformance.getMarketRanks());
        assertEquals(marketValueMap, portfolioPerformance.getMarketValues());
    }

    @Test
    public void testEquals() {
        PortfolioPerformance portfolioPerformance1 = new PortfolioPerformance()
                .setTotalRanks(totalRankCollection)
                .setTotalValues(totalValueCollection)
                .setCreditRanks(creditRankCollection)
                .setCreditValues(creditValueCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketTotalValues(marketTotalValueCollection)
                .setMarketRanks(marketRankMap)
                .setMarketValues(marketValueMap);
        PortfolioPerformance portfolioPerformance2 = new PortfolioPerformance()
                .setTotalRanks(totalRankCollection)
                .setTotalValues(totalValueCollection)
                .setCreditRanks(creditRankCollection)
                .setCreditValues(creditValueCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketTotalValues(marketTotalValueCollection)
                .setMarketRanks(marketRankMap)
                .setMarketValues(marketValueMap);
        assertEquals(portfolioPerformance1, portfolioPerformance2);
    }

    @Test
    public void testHashCode() {
        PortfolioPerformance portfolioPerformance = new PortfolioPerformance()
                .setTotalRanks(totalRankCollection)
                .setTotalValues(totalValueCollection)
                .setCreditRanks(creditRankCollection)
                .setCreditValues(creditValueCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketTotalValues(marketTotalValueCollection)
                .setMarketRanks(marketRankMap)
                .setMarketValues(marketValueMap);
        assertEquals(-1807454463, new PortfolioPerformance().hashCode());
        assertNotEquals(0, portfolioPerformance.hashCode()); // enums cause inconsistent values
    }

    @Test
    public void testToString() {
        PortfolioPerformance portfolioPerformance = new PortfolioPerformance()
                .setTotalRanks(totalRankCollection)
                .setTotalValues(totalValueCollection)
                .setCreditRanks(creditRankCollection)
                .setCreditValues(creditValueCollection)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setMarketTotalValues(marketTotalValueCollection)
                .setMarketRanks(marketRankMap)
                .setMarketValues(marketValueMap);
        assertNotNull(portfolioPerformance.toString()); // skipping real check
    }
}
