package vstocks.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PortfolioValueCollection {
    private List<PortfolioValue> values;
    private Map<DeltaInterval, Delta> deltas;

    public PortfolioValueCollection() {
    }

    public List<PortfolioValue> getValues() {
        return values;
    }

    public PortfolioValueCollection setValues(List<PortfolioValue> values) {
        this.values = values;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public PortfolioValueCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioValueCollection that = (PortfolioValueCollection) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, deltas);
    }

    @Override
    public String toString() {
        return "PortfolioValueCollection{" +
                "values=" + values +
                ", deltas=" + deltas +
                '}';
    }
}
