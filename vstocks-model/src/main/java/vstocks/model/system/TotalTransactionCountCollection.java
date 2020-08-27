package vstocks.model.system;

import vstocks.model.Delta;

import java.util.List;
import java.util.Objects;

public class TotalTransactionCountCollection {
    private List<TotalTransactionCount> counts;
    private List<Delta> deltas;

    public TotalTransactionCountCollection() {
    }

    public List<TotalTransactionCount> getCounts() {
        return counts;
    }

    public TotalTransactionCountCollection setCounts(List<TotalTransactionCount> counts) {
        this.counts = counts;
        return this;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public TotalTransactionCountCollection setDeltas(List<Delta> deltas) {
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
