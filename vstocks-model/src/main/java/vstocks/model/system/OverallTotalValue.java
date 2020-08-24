package vstocks.model.system;

import java.time.Instant;
import java.util.Objects;

public class OverallTotalValue {
    private Instant timestamp;
    private long value;

    public OverallTotalValue() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public OverallTotalValue setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getValue() {
        return value;
    }

    public OverallTotalValue setValue(long value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverallTotalValue that = (OverallTotalValue) o;
        return value == that.value &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, value);
    }

    @Override
    public String toString() {
        return "OverallTotalValue{" +
                "timestamp=" + timestamp +
                ", value=" + value +
                '}';
    }
}
