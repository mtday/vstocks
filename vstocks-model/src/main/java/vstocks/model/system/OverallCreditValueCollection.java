package vstocks.model.system;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OverallCreditValueCollection {
    private List<OverallCreditValue> values;
    private Map<DeltaInterval, Delta> deltas;

    public OverallCreditValueCollection() {
    }

    public List<OverallCreditValue> getValues() {
        return values;
    }

    public OverallCreditValueCollection setValues(List<OverallCreditValue> values) {
        this.values = values;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public OverallCreditValueCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverallCreditValueCollection that = (OverallCreditValueCollection) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, deltas);
    }

    @Override
    public String toString() {
        return "OverallCreditValueCollection{" +
                "values=" + values +
                ", deltas=" + deltas +
                '}';
    }
}
