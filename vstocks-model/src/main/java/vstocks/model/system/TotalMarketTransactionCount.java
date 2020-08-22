package vstocks.model.system;

import vstocks.model.Market;

import java.time.Instant;
import java.util.Objects;

public class TotalMarketTransactionCount {
    private Instant timestamp;
    private Market market;
    private long count;

    public TotalMarketTransactionCount() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public TotalMarketTransactionCount setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Market getMarket() {
        return market;
    }

    public TotalMarketTransactionCount setMarket(Market market) {
        this.market = market;
        return this;
    }

    public long getCount() {
        return count;
    }

    public TotalMarketTransactionCount setCount(long count) {
        this.count = count;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalMarketTransactionCount that = (TotalMarketTransactionCount) o;
        return count == that.count &&
                Objects.equals(timestamp, that.timestamp) &&
                market == that.market;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, market, count);
    }

    @Override
    public String toString() {
        return "TotalMarketTransactionCount{" +
                "timestamp=" + timestamp +
                ", market=" + market +
                ", count=" + count +
                '}';
    }
}
