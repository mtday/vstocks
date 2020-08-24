package vstocks.model.portfolio;

import vstocks.model.Market;

import java.util.Map;
import java.util.Objects;

public class PortfolioPerformance {
    private TotalRankCollection totalRanks;
    private TotalValueCollection totalValues;
    private CreditRankCollection creditRanks;
    private CreditValueCollection creditValues;
    private MarketTotalRankCollection marketTotalRanks;
    private MarketTotalValueCollection marketTotalValues;
    private Map<Market, MarketRankCollection> marketRanks;
    private Map<Market, MarketValueCollection> marketValues;

    public PortfolioPerformance() {
    }

    public TotalRankCollection getTotalRanks() {
        return totalRanks;
    }

    public PortfolioPerformance setTotalRanks(TotalRankCollection totalRanks) {
        this.totalRanks = totalRanks;
        return this;
    }

    public TotalValueCollection getTotalValues() {
        return totalValues;
    }

    public PortfolioPerformance setTotalValues(TotalValueCollection totalValues) {
        this.totalValues = totalValues;
        return this;
    }

    public CreditRankCollection getCreditRanks() {
        return creditRanks;
    }

    public PortfolioPerformance setCreditRanks(CreditRankCollection creditRanks) {
        this.creditRanks = creditRanks;
        return this;
    }

    public CreditValueCollection getCreditValues() {
        return creditValues;
    }

    public PortfolioPerformance setCreditValues(CreditValueCollection creditValues) {
        this.creditValues = creditValues;
        return this;
    }

    public MarketTotalRankCollection getMarketTotalRanks() {
        return marketTotalRanks;
    }

    public PortfolioPerformance setMarketTotalRanks(MarketTotalRankCollection marketTotalRanks) {
        this.marketTotalRanks = marketTotalRanks;
        return this;
    }

    public MarketTotalValueCollection getMarketTotalValues() {
        return marketTotalValues;
    }

    public PortfolioPerformance setMarketTotalValues(MarketTotalValueCollection marketTotalValues) {
        this.marketTotalValues = marketTotalValues;
        return this;
    }

    public Map<Market, MarketRankCollection> getMarketRanks() {
        return marketRanks;
    }

    public PortfolioPerformance setMarketRanks(Map<Market, MarketRankCollection> marketRanks) {
        this.marketRanks = marketRanks;
        return this;
    }

    public Map<Market, MarketValueCollection> getMarketValues() {
        return marketValues;
    }

    public PortfolioPerformance setMarketValues(Map<Market, MarketValueCollection> marketValues) {
        this.marketValues = marketValues;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioPerformance that = (PortfolioPerformance) o;
        return Objects.equals(totalRanks, that.totalRanks) &&
                Objects.equals(totalValues, that.totalValues) &&
                Objects.equals(creditRanks, that.creditRanks) &&
                Objects.equals(creditValues, that.creditValues) &&
                Objects.equals(marketTotalRanks, that.marketTotalRanks) &&
                Objects.equals(marketTotalValues, that.marketTotalValues) &&
                Objects.equals(marketRanks, that.marketRanks) &&
                Objects.equals(marketValues, that.marketValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalRanks, totalValues, creditRanks, creditValues, marketTotalRanks, marketTotalValues,
                marketRanks, marketValues);
    }

    @Override
    public String toString() {
        return "PortfolioPerformance{" +
                "totalRanks=" + totalRanks +
                ", totalValues=" + totalValues +
                ", creditRanks=" + creditRanks +
                ", creditValues=" + creditValues +
                ", marketTotalRanks=" + marketTotalRanks +
                ", marketTotalValues=" + marketTotalValues +
                ", marketRanks=" + marketRanks +
                ", marketValues=" + marketValues +
                '}';
    }
}