package vstocks.model.system;

import java.time.Instant;
import java.util.Objects;

public class TotalUserCount {
    private Instant timestamp;
    private long count;

    public TotalUserCount() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public TotalUserCount setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public long getCount() {
        return count;
    }

    public TotalUserCount setCount(long count) {
        this.count = count;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalUserCount that = (TotalUserCount) o;
        return count == that.count && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, count);
    }

    @Override
    public String toString() {
        return "TotalUserCount{" +
                "timestamp=" + timestamp +
                ", count=" + count +
                '}';
    }
}
