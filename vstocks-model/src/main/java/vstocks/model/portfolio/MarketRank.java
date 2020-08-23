package vstocks.model.portfolio;

import vstocks.model.Market;

import java.time.Instant;
import java.util.Objects;

public class MarketRank {
    private long batch;
    private String userId;
    private Market market;
    private Instant timestamp;
    private long rank;
    private long value;

    public MarketRank() {
    }

    public long getBatch() {
        return batch;
    }

    public MarketRank setBatch(long batch) {
        this.batch = batch;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public MarketRank setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Market getMarket() {
        return market;
    }

    public MarketRank setMarket(Market market) {
        this.market = market;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public MarketRank setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getRank() {
        return rank;
    }

    public MarketRank setRank(long rank) {
        this.rank = rank;
        return this;
    }

    public long getValue() {
        return value;
    }

    public MarketRank setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketRank that = (MarketRank) o;
        return batch == that.batch &&
                rank == that.rank &&
                value == that.value &&
                Objects.equals(userId, that.userId) &&
                market == that.market &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batch, userId, market, timestamp, rank, value);
    }

    @Override
    public String toString() {
        return "MarketRank{" +
                "batch=" + batch +
                ", userId='" + userId + '\'' +
                ", market=" + market +
                ", timestamp=" + timestamp +
                ", rank=" + rank +
                ", value=" + value +
                '}';
    }
}
