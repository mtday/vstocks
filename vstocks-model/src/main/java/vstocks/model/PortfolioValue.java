package vstocks.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class PortfolioValue {
    private String userId;
    private Instant timestamp;
    private long credits;
    private Map<Market, Long> marketValues;
    private long total;
    private Map<DeltaInterval, Delta> deltas;

    public PortfolioValue() {
    }

    public String getUserId() {
        return userId;
    }

    public PortfolioValue setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public PortfolioValue setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getCredits() {
        return credits;
    }

    public PortfolioValue setCredits(long credits) {
        this.credits = credits;
        return this;
    }

    public Map<Market, Long> getMarketValues() {
        return marketValues;
    }

    public PortfolioValue setMarketValues(Map<Market, Long> marketValues) {
        this.marketValues = marketValues;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public PortfolioValue setTotal(long total) {
        this.total = total;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public PortfolioValue setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioValue that = (PortfolioValue) o;
        return credits == that.credits &&
                total == that.total &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(marketValues, that.marketValues) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, timestamp, credits, marketValues, total, deltas);
    }

    @Override
    public String toString() {
        return "PortfolioValue{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", credits=" + credits +
                ", marketValues=" + marketValues +
                ", total=" + total +
                ", deltas=" + deltas +
                '}';
    }
}
