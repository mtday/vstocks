package vstocks.model.system;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ActiveMarketTransactionCountCollection {
    private List<ActiveMarketTransactionCount> counts;
    private Map<DeltaInterval, Delta> deltas;

    public ActiveMarketTransactionCountCollection() {
    }

    public List<ActiveMarketTransactionCount> getCounts() {
        return counts;
    }

    public ActiveMarketTransactionCountCollection setCounts(List<ActiveMarketTransactionCount> counts) {
        this.counts = counts;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public ActiveMarketTransactionCountCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveMarketTransactionCountCollection that = (ActiveMarketTransactionCountCollection) o;
        return Objects.equals(counts, that.counts) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(counts, deltas);
    }

    @Override
    public String toString() {
        return "ActiveMarketTransactionCountCollection{" +
                "counts=" + counts +
                ", deltas=" + deltas +
                '}';
    }
}
