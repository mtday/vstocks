package vstocks.model;

import java.time.Instant;
import java.util.Objects;

public class PortfolioValueRank {
    private String userId;
    private Instant timestamp;
    private int rank;

    public PortfolioValueRank() {
    }

    public String getUserId() {
        return userId;
    }

    public PortfolioValueRank setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public PortfolioValueRank setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getRank() {
        return rank;
    }

    public PortfolioValueRank setRank(int rank) {
        this.rank = rank;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioValueRank that = (PortfolioValueRank) o;
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
        return "PortfolioValueRank{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", rank=" + rank +
                '}';
    }
}
