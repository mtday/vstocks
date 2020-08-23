package vstocks.model.portfolio;

import vstocks.model.Market;

import java.time.Instant;
import java.util.Objects;

public class MarketValue {
    private long batch;
    private String userId;
    private Market market;
    private Instant timestamp;
    private long value;

    public MarketValue() {
    }

    public long getBatch() {
        return batch;
    }

    public MarketValue setBatch(long batch) {
        this.batch = batch;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public MarketValue setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Market getMarket() {
        return market;
    }

    public MarketValue setMarket(Market market) {
        this.market = market;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public MarketValue setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getValue() {
        return value;
    }

    public MarketValue setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketValue that = (MarketValue) o;
        return batch == that.batch &&
                value == that.value &&
                Objects.equals(userId, that.userId) &&
                market == that.market &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batch, userId, market, timestamp, value);
    }

    @Override
    public String toString() {
        return "MarketValue{" +
                "batch=" + batch +
                ", userId='" + userId + '\'' +
                ", market=" + market +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
