package vstocks.model.portfolio;

import vstocks.model.Market;

import java.util.Map;
import java.util.Objects;

public class PortfolioPerformance {
    private TotalRankCollection totalRanks;
    private CreditRankCollection creditRanks;
    private MarketTotalRankCollection marketTotalRanks;
    private Map<Market, MarketRankCollection> marketRanks;

    public PortfolioPerformance() {
    }

    public TotalRankCollection getTotalRanks() {
        return totalRanks;
    }

    public PortfolioPerformance setTotalRanks(TotalRankCollection totalRanks) {
        this.totalRanks = totalRanks;
        return this;
    }

    public CreditRankCollection getCreditRanks() {
        return creditRanks;
    }

    public PortfolioPerformance setCreditRanks(CreditRankCollection creditRanks) {
        this.creditRanks = creditRanks;
        return this;
    }

    public MarketTotalRankCollection getMarketTotalRanks() {
        return marketTotalRanks;
    }

    public PortfolioPerformance setMarketTotalRanks(MarketTotalRankCollection marketTotalRanks) {
        this.marketTotalRanks = marketTotalRanks;
        return this;
    }

    public Map<Market, MarketRankCollection> getMarketRanks() {
        return marketRanks;
    }

    public PortfolioPerformance setMarketRanks(Map<Market, MarketRankCollection> marketRanks) {
        this.marketRanks = marketRanks;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioPerformance that = (PortfolioPerformance) o;
        return Objects.equals(totalRanks, that.totalRanks) &&
                Objects.equals(creditRanks, that.creditRanks) &&
                Objects.equals(marketTotalRanks, that.marketTotalRanks) &&
                Objects.equals(marketRanks, that.marketRanks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalRanks, creditRanks, marketTotalRanks, marketRanks);
    }

    @Override
    public String toString() {
        return "PortfolioPerformance{" +
                "totalRanks=" + totalRanks +
                ", creditRanks=" + creditRanks +
                ", marketTotalRanks=" + marketTotalRanks +
                ", marketRanks=" + marketRanks +
                '}';
    }
}
