package vstocks.model.portfolio;

import java.util.List;
import java.util.Objects;

public class PortfolioValue {
    private PortfolioValueSummary summary;
    private CreditRankCollection creditRanks;
    private MarketTotalRankCollection marketTotalRanks;
    private List<MarketRankCollection> marketRanks;
    private TotalRankCollection totalRanks;

    public PortfolioValue() {
    }

    public PortfolioValueSummary getSummary() {
        return summary;
    }

    public PortfolioValue setSummary(PortfolioValueSummary summary) {
        this.summary = summary;
        return this;
    }

    public CreditRankCollection getCreditRanks() {
        return creditRanks;
    }

    public PortfolioValue setCreditRanks(CreditRankCollection creditRanks) {
        this.creditRanks = creditRanks;
        return this;
    }

    public MarketTotalRankCollection getMarketTotalRanks() {
        return marketTotalRanks;
    }

    public PortfolioValue setMarketTotalRanks(MarketTotalRankCollection marketTotalRanks) {
        this.marketTotalRanks = marketTotalRanks;
        return this;
    }

    public List<MarketRankCollection> getMarketRanks() {
        return marketRanks;
    }

    public PortfolioValue setMarketRanks(List<MarketRankCollection> marketRanks) {
        this.marketRanks = marketRanks;
        return this;
    }

    public TotalRankCollection getTotalRanks() {
        return totalRanks;
    }

    public PortfolioValue setTotalRanks(TotalRankCollection totalRanks) {
        this.totalRanks = totalRanks;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioValue that = (PortfolioValue) o;
        return Objects.equals(summary, that.summary) &&
                Objects.equals(creditRanks, that.creditRanks) &&
                Objects.equals(marketTotalRanks, that.marketTotalRanks) &&
                Objects.equals(marketRanks, that.marketRanks) &&
                Objects.equals(totalRanks, that.totalRanks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summary, creditRanks, marketTotalRanks, marketRanks, totalRanks);
    }

    @Override
    public String toString() {
        return "PortfolioValue{" +
                "summary=" + summary +
                ", creditRanks=" + creditRanks +
                ", marketTotalRanks=" + marketTotalRanks +
                ", marketRanks=" + marketRanks +
                ", totalRanks=" + totalRanks +
                '}';
    }
}
