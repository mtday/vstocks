package vstocks.model.portfolio;

import java.util.List;
import java.util.Objects;

public class PortfolioValueSummary {
    private String userId;
    private long credits;
    private long marketTotal;
    private List<MarketValue> marketValues;
    private long total;

    public PortfolioValueSummary() {
    }

    public String getUserId() {
        return userId;
    }

    public PortfolioValueSummary setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public long getCredits() {
        return credits;
    }

    public PortfolioValueSummary setCredits(long credits) {
        this.credits = credits;
        return this;
    }

    public long getMarketTotal() {
        return marketTotal;
    }

    public PortfolioValueSummary setMarketTotal(long marketTotal) {
        this.marketTotal = marketTotal;
        return this;
    }

    public List<MarketValue> getMarketValues() {
        return marketValues;
    }

    public PortfolioValueSummary setMarketValues(List<MarketValue> marketValues) {
        this.marketValues = marketValues;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public PortfolioValueSummary setTotal(long total) {
        this.total = total;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioValueSummary that = (PortfolioValueSummary) o;
        return credits == that.credits &&
                marketTotal == that.marketTotal &&
                total == that.total &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(marketValues, that.marketValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, credits, marketTotal, marketValues, total);
    }

    @Override
    public String toString() {
        return "PortfolioValueSummary{" +
                "userId='" + userId + '\'' +
                ", credits=" + credits +
                ", marketTotal=" + marketTotal +
                ", marketValues=" + marketValues +
                ", total=" + total +
                '}';
    }
}
