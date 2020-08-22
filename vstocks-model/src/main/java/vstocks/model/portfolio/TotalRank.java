package vstocks.model.portfolio;

import java.time.Instant;
import java.util.Objects;

public class TotalRank {
    private String userId;
    private Instant timestamp;
    private long rank;

    public TotalRank() {
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
        TotalRank that = (TotalRank) o;
        return rank == that.rank &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, timestamp, rank);
    }

    @Override
    public String toString() {
        return "PortfolioTotalRank{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", rank=" + rank +
                '}';
    }
}
