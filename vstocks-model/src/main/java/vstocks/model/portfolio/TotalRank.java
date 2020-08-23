package vstocks.model.portfolio;

import java.time.Instant;
import java.util.Objects;

public class TotalRank {
    private long batch;
    private String userId;
    private Instant timestamp;
    private long rank;

    public TotalRank() {
    }

    public long getBatch() {
        return batch;
    }

    public TotalRank setBatch(long batch) {
        this.batch = batch;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public TotalRank setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public TotalRank setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getRank() {
        return rank;
    }

    public TotalRank setRank(long rank) {
        this.rank = rank;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalRank totalRank = (TotalRank) o;
        return batch == totalRank.batch &&
                rank == totalRank.rank &&
                Objects.equals(userId, totalRank.userId) &&
                Objects.equals(timestamp, totalRank.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(batch, userId, timestamp, rank);
    }

    @Override
    public String toString() {
        return "TotalRank{" +
                "batch=" + batch +
                ", userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", rank=" + rank +
                '}';
    }
}
