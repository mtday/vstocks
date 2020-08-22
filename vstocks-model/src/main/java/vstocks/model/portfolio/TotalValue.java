package vstocks.model.portfolio;

import java.time.Instant;
import java.util.Objects;

public class TotalValue {
    private String userId;
    private Instant timestamp;
    private long value;

    public TotalValue() {
    }

    public String getUserId() {
        return userId;
    }

    public TotalValue setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public TotalValue setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getValue() {
        return value;
    }

    public TotalValue setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalValue that = (TotalValue) o;
        return value == that.value &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, timestamp, value);
    }

    @Override
    public String toString() {
        return "PortfolioTotalValue{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
