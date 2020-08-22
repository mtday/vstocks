package vstocks.model.system;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TotalMarketTransactionCountCollection {
    private List<TotalMarketTransactionCount> counts;
    private Map<DeltaInterval, Delta> deltas;

    public TotalMarketTransactionCountCollection() {
    }

    public List<TotalMarketTransactionCount> getCounts() {
        return counts;
    }

    public TotalMarketTransactionCountCollection setCounts(List<TotalMarketTransactionCount> counts) {
        this.counts = counts;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public TotalMarketTransactionCountCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalMarketTransactionCountCollection that = (TotalMarketTransactionCountCollection) o;
        return Objects.equals(counts, that.counts) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(counts, deltas);
    }

    @Override
    public String toString() {
        return "TotalMarketTransactionCountCollection{" +
                "counts=" + counts +
                ", deltas=" + deltas +
                '}';
    }
}
