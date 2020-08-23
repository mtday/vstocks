package vstocks.model.system;

import vstocks.model.Market;

import java.time.Instant;
import java.util.Objects;

public class ActiveMarketTransactionCount {
    private Market market;
    private Instant timestamp;
    private long count;

    public ActiveMarketTransactionCount() {
    }

    public Market getMarket() {
        return market;
    }

    public ActiveMarketTransactionCount setMarket(Market market) {
        this.market = market;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public ActiveMarketTransactionCount setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getCount() {
        return count;
    }

    public ActiveMarketTransactionCount setCount(long count) {
        this.count = count;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveMarketTransactionCount that = (ActiveMarketTransactionCount) o;
        return count == that.count &&
                market == that.market &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market, timestamp, count);
    }

    @Override
    public String toString() {
        return "ActiveMarketTransactionCount{" +
                "market=" + market +
                ", timestamp=" + timestamp +
                ", count=" + count +
                '}';
    }
}
