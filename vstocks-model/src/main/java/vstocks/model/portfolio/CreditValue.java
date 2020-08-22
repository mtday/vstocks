package vstocks.model.portfolio;

import java.time.Instant;
import java.util.Objects;

public class CreditValue {
    private String userId;
    private Instant timestamp;
    private long value;

    public CreditValue() {
    }

    public String getUserId() {
        return userId;
    }

    public CreditValue setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public CreditValue setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getValue() {
        return value;
    }

    public CreditValue setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditValue that = (CreditValue) o;
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
        return "PortfolioCreditValue{" +
                "userId='" + userId + '\'' +
                ", timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
