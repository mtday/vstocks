package vstocks.model.system;

import vstocks.model.Delta;

import java.util.List;
import java.util.Objects;

public class OverallTotalValueCollection {
    private List<OverallTotalValue> values;
    private List<Delta> deltas;

    public OverallTotalValueCollection() {
    }

    public List<OverallTotalValue> getValues() {
        return values;
    }

    public OverallTotalValueCollection setValues(List<OverallTotalValue> values) {
        this.values = values;
        return this;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public OverallTotalValueCollection setDeltas(List<Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverallTotalValueCollection that = (OverallTotalValueCollection) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, deltas);
    }

    @Override
    public String toString() {
        return "OverallTotalValueCollection{" +
                "values=" + values +
                ", deltas=" + deltas +
                '}';
    }
}
