package vstocks.model;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

public class PortfolioValueRank {
    private String userId;
    private Instant timestamp;
    private long rank;
    private Map<DeltaInterval, Delta> deltas;

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

    public long getRank() {
        return rank;
    }

    public PortfolioValueRank setRank(long rank) {
        this.rank = rank;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public PortfolioValueRank setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioValueRank that = (PortfolioValueRank) o;
        return rank == that.rank &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, timestamp, rank, deltas);
    }

    @Override
    public String toString() {
        return "PortfolioValueRank{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", rank=" + rank +
                ", deltas=" + deltas +
                '}';
    }
}
