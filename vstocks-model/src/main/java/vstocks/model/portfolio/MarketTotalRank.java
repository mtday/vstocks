package vstocks.model.portfolio;

import java.time.Instant;
import java.util.Objects;

public class MarketTotalRank {
    private long batch;
    private String userId;
    private Instant timestamp;
    private long rank;

    public MarketTotalRank() {
    }

    public long getBatch() {
        return batch;
    }

    public MarketTotalRank setBatch(long batch) {
        this.batch = batch;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public MarketTotalRank setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public MarketTotalRank setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getRank() {
        return rank;
    }

    public MarketTotalRank setRank(long rank) {
        this.rank = rank;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketTotalRank that = (MarketTotalRank) o;
        return batch == that.batch &&
                rank == that.rank &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batch, userId, timestamp, rank);
    }

    @Override
    public String toString() {
        return "MarketTotalRank{" +
                "batch=" + batch +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", rank=" + rank +
                '}';
    }
}
