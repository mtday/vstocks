package vstocks.model.system;

import vstocks.model.Delta;

import java.util.List;
import java.util.Objects;

public class OverallMarketTotalValueCollection {
    private List<OverallMarketTotalValue> values;
    private List<Delta> deltas;

    public OverallMarketTotalValueCollection() {
    }

    public List<OverallMarketTotalValue> getValues() {
        return values;
    }

    public OverallMarketTotalValueCollection setValues(List<OverallMarketTotalValue> values) {
        this.values = values;
        return this;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public OverallMarketTotalValueCollection setDeltas(List<Delta> deltas) {
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
