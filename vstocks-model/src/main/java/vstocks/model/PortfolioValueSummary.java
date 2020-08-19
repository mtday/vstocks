package vstocks.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class PortfolioValueSummary {
    private Instant timestamp;
    private long credits;
    private Map<Market, Long> marketValues;
    private long total;

    public PortfolioValueSummary() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public PortfolioValueSummary setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getCredits() {
        return credits;
    }

    public PortfolioValueSummary setCredits(long credits) {
        this.credits = credits;
        return this;
    }

    public Map<Market, Long> getMarketValues() {
        return marketValues;
    }

    public PortfolioValueSummary setMarketValues(Map<Market, Long> marketValues) {
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
                total == that.total &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(marketValues, that.marketValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, credits, marketValues, total);
    }

    @Override
    public String toString() {
        return "PortfolioValueSummary{" +
                "timestamp=" + timestamp +
                ", credits=" + credits +
                ", marketValues=" + marketValues +
                ", total=" + total +
                '}';
    }
}
