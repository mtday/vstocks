package vstocks.model;

import java.util.Map;
import java.util.Objects;

public class PortfolioValue {
    private String userId;
    private long credits;
    private Map<Market, Long> marketValues;
    private long total;

    public PortfolioValue() {
    }

    public String getUserId() {
        return userId;
    }

    public PortfolioValue setUserId(String userId) {
        this.userId = userId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioValue that = (PortfolioValue) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    @Override
    public String toString() {
        return "PortfolioValue{" +
                "userId='" + userId + '\'' +
                ", credits=" + credits +
                ", marketValues=" + marketValues +
                ", total=" + total +
                '}';
    }
}
