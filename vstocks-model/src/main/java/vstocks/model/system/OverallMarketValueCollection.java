package vstocks.model.system;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OverallMarketValueCollection {
    private List<OverallMarketValue> values;
    private Map<DeltaInterval, Delta> deltas;

    public OverallMarketValueCollection() {
    }

    public List<OverallMarketValue> getValues() {
        return values;
    }

    public OverallMarketValueCollection setValues(List<OverallMarketValue> values) {
        this.values = values;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public OverallMarketValueCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverallMarketValueCollection that = (OverallMarketValueCollection) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, deltas);
    }

    @Override
    public String toString() {
        return "OverallMarketValueCollection{" +
                "values=" + values +
                ", deltas=" + deltas +
                '}';
    }
}
