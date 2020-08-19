package vstocks.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PortfolioValueSummaryCollection {
    private List<PortfolioValueSummary> summaries;
    private Map<DeltaInterval, Delta> deltas;

    public PortfolioValueSummaryCollection() {
    }

    public List<PortfolioValueSummary> getSummaries() {
        return summaries;
    }

    public PortfolioValueSummaryCollection setSummaries(List<PortfolioValueSummary> summaries) {
        this.summaries = summaries;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public PortfolioValueSummaryCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioValueSummaryCollection that = (PortfolioValueSummaryCollection) o;
        return Objects.equals(summaries, that.summaries) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summaries, deltas);
    }

    @Override
    public String toString() {
        return "PortfolioValueSummaryCollection{" +
                "summaries=" + summaries +
                ", deltas=" + deltas +
                '}';
    }
}
