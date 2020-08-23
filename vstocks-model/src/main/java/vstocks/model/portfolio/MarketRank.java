package vstocks.model.portfolio;

import vstocks.model.Market;

import java.time.Instant;
import java.util.Objects;

public class MarketRank {
    private String userId;
    private Market market;
    private Instant timestamp;
    private long rank;

    public MarketRank() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketRank that = (MarketRank) o;
        return rank == that.rank &&
                Objects.equals(userId, that.userId) &&
                market == that.market &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, market, timestamp, rank);
    }

    @Override
    public String toString() {
        return "MarketRank{" +
                "userId='" + userId + '\'' +
                ", market=" + market +
                ", timestamp=" + timestamp +
                ", rank=" + rank +
                '}';
    }
}
