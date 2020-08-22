package vstocks.model.system;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TotalUserCountCollection {
    private List<TotalUserCount> counts;
    private Map<DeltaInterval, Delta> deltas;

    public TotalUserCountCollection() {
    }

    public List<TotalUserCount> getCounts() {
        return counts;
    }

    public TotalUserCountCollection setCounts(List<TotalUserCount> counts) {
        this.counts = counts;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public TotalUserCountCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalUserCountCollection that = (TotalUserCountCollection) o;
        return Objects.equals(counts, that.counts) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(counts, deltas);
    }

    @Override
    public String toString() {
        return "TotalUserCountCollection{" +
                "counts=" + counts +
                ", deltas=" + deltas +
                '}';
    }
}
