package vstocks.model.system;

import java.time.Instant;
import java.util.Objects;

public class TotalTransactionCount {
    private Instant timestamp;
    private long count;

    public TotalTransactionCount() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public TotalTransactionCount setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getCount() {
        return count;
    }

    public TotalTransactionCount setCount(long count) {
        this.count = count;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalTransactionCount that = (TotalTransactionCount) o;
        return count == that.count &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, count);
    }

    @Override
    public String toString() {
        return "TotalTransactionCount{" +
                "timestamp=" + timestamp +
                ", count=" + count +
                '}';
    }
}
