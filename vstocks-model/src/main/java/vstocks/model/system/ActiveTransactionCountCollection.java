package vstocks.model.system;

import vstocks.model.Delta;

import java.util.List;
import java.util.Objects;

public class ActiveTransactionCountCollection {
    private List<ActiveTransactionCount> counts;
    private List<Delta> deltas;

    public ActiveTransactionCountCollection() {
    }

    public List<ActiveTransactionCount> getCounts() {
        return counts;
    }

    public ActiveTransactionCountCollection setCounts(List<ActiveTransactionCount> counts) {
        this.counts = counts;
        return this;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public ActiveTransactionCountCollection setDeltas(List<Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveTransactionCountCollection that = (ActiveTransactionCountCollection) o;
        return Objects.equals(counts, that.counts) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(counts, deltas);
    }

    @Override
    public String toString() {
        return "ActiveTransactionCountCollection{" +
                "counts=" + counts +
                ", deltas=" + deltas +
                '}';
    }
}
