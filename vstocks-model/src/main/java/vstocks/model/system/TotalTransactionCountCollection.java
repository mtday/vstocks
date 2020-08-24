package vstocks.model.system;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TotalTransactionCountCollection {
    private List<TotalTransactionCount> counts;
    private Map<DeltaInterval, Delta> deltas;

    public TotalTransactionCountCollection() {
    }

    public List<TotalTransactionCount> getCounts() {
        return counts;
    }

    public TotalTransactionCountCollection setCounts(List<TotalTransactionCount> counts) {
        this.counts = counts;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public TotalTransactionCountCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalTransactionCountCollection that = (TotalTransactionCountCollection) o;
        return Objects.equals(counts, that.counts) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(counts, deltas);
    }

    @Override
    public String toString() {
        return "TotalTransactionCountCollection{" +
                "counts=" + counts +
                ", deltas=" + deltas +
                '}';
    }
}