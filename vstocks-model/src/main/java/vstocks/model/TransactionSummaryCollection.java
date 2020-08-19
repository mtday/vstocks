package vstocks.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TransactionSummaryCollection {
    private List<TransactionSummary> summaries;
    private Map<DeltaInterval, Delta> deltas;

    public TransactionSummaryCollection() {
    }

    public List<TransactionSummary> getSummaries() {
        return summaries;
    }

    public TransactionSummaryCollection setSummaries(List<TransactionSummary> summaries) {
        this.summaries = summaries;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public TransactionSummaryCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionSummaryCollection that = (TransactionSummaryCollection) o;
        return Objects.equals(summaries, that.summaries) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summaries, deltas);
    }

    @Override
    public String toString() {
        return "TransactionSummaryCollection{" +
                "summaries=" + summaries +
                ", deltas=" + deltas +
                '}';
    }
}
