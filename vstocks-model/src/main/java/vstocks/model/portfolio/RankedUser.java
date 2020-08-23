package vstocks.model.portfolio;

import vstocks.model.User;

import java.time.Instant;
import java.util.Objects;

public class RankedUser {
    private User user;
    private long batch;
    private Instant timestamp;
    private long rank;

    public RankedUser() {
    }

    public User getUser() {
        return user;
    }

    public RankedUser setUser(User user) {
        this.user = user;
        return this;
    }

    public long getBatch() {
        return batch;
    }

    public RankedUser setBatch(long batch) {
        this.batch = batch;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public RankedUser setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getRank() {
        return rank;
    }

    public RankedUser setRank(long rank) {
        this.rank = rank;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RankedUser that = (RankedUser) o;
        return batch == that.batch &&
                rank == that.rank &&
                Objects.equals(user, that.user) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, batch, timestamp, rank);
    }

    @Override
    public String toString() {
        return "RankedUser{" +
                "user=" + user +
                ", batch=" + batch +
                ", timestamp=" + timestamp +
                ", rank=" + rank +
                '}';
    }
}
