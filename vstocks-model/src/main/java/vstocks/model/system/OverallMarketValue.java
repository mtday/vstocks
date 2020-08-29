package vstocks.model.system;

import vstocks.model.Market;

import java.time.Instant;
import java.util.Objects;

public class OverallMarketValue {
    private Market market;
    private Instant timestamp;
    private long value;

    public OverallMarketValue() {
    }

    public Market getMarket() {
        return market;
    }

    public OverallMarketValue setMarket(Market market) {
        this.market = market;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public OverallMarketValue setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getValue() {
        return value;
    }

    public OverallMarketValue setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverallMarketValue that = (OverallMarketValue) o;
        return value == that.value &&
                market == that.market &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market.name(), timestamp, value);
    }

    @Override
    public String toString() {
        return "OverallMarketValue{" +
                "market=" + market +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
