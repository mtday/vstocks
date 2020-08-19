package vstocks.model;

import java.time.Instant;
import java.util.Objects;

public class UserCount {
    private Instant timestamp;
    private long users;

    public UserCount() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public UserCount setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getUsers() {
        return users;
    }

    public UserCount setUsers(long users) {
        this.users = users;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCount that = (UserCount) o;
        return users == that.users && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, users);
    }

    @Override
    public String toString() {
        return "UserCount{" +
                "timestamp=" + timestamp +
                ", users=" + users +
                '}';
    }
}
