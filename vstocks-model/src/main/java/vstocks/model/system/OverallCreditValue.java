package vstocks.model.system;

import java.time.Instant;
import java.util.Objects;

public class OverallCreditValue {
    private Instant timestamp;
    private long value;

    public OverallCreditValue() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public OverallCreditValue setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getValue() {
        return value;
    }

    public OverallCreditValue setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverallCreditValue that = (OverallCreditValue) o;
        return value == that.value &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, value);
    }

    @Override
    public String toString() {
        return "OverallCreditValue{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
