package vstocks.model.system;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ActiveUserCountCollection {
    private List<ActiveUserCount> counts;
    private Map<DeltaInterval, Delta> deltas;

    public ActiveUserCountCollection() {
    }

    public List<ActiveUserCount> getCounts() {
        return counts;
    }

    public ActiveUserCountCollection setCounts(List<ActiveUserCount> counts) {
        this.counts = counts;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public ActiveUserCountCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveUserCountCollection that = (ActiveUserCountCollection) o;
        return Objects.equals(counts, that.counts) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(counts, deltas);
    }

    @Override
    public String toString() {
        return "ActiveUserCountCollection{" +
                "counts=" + counts +
                ", deltas=" + deltas +
                '}';
    }
}
