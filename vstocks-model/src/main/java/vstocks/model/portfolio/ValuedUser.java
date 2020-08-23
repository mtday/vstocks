package vstocks.model.portfolio;

import vstocks.model.User;

import java.time.Instant;
import java.util.Objects;

public class ValuedUser {
    private User user;
    private long batch;
    private Instant timestamp;
    private long value;

    public ValuedUser() {
    }

    public User getUser() {
        return user;
    }

    public ValuedUser setUser(User user) {
        this.user = user;
        return this;
    }

    public long getBatch() {
        return batch;
    }

    public ValuedUser setBatch(long batch) {
        this.batch = batch;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public ValuedUser setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getValue() {
        return value;
    }

    public ValuedUser setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValuedUser that = (ValuedUser) o;
        return batch == that.batch &&
                value == that.value &&
                Objects.equals(user, that.user) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, batch, timestamp, value);
    }

    @Override
    public String toString() {
        return "ValuedUser{" +
                "user=" + user +
                ", batch=" + batch +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
