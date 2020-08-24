package vstocks.model.system;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OverallMarketTotalValueCollection {
    private List<OverallMarketTotalValue> values;
    private Map<DeltaInterval, Delta> deltas;

    public OverallMarketTotalValueCollection() {
    }

    public List<OverallMarketTotalValue> getValues() {
        return values;
    }

    public OverallMarketTotalValueCollection setValues(List<OverallMarketTotalValue> values) {
        this.values = values;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public OverallMarketTotalValueCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverallMarketTotalValueCollection that = (OverallMarketTotalValueCollection) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, deltas);
    }

    @Override
    public String toString() {
        return "OverallMarketTotalValueCollection{" +
                "values=" + values +
                ", deltas=" + deltas +
                '}';
    }
}
