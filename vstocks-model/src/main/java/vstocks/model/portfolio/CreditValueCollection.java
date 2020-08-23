package vstocks.model.portfolio;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreditValueCollection {
    private List<CreditValue> values;
    private Map<DeltaInterval, Delta> deltas;

    public CreditValueCollection() {
    }

    public List<CreditValue> getValues() {
        return values;
    }

    public CreditValueCollection setValues(List<CreditValue> values) {
        this.values = values;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public CreditValueCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditValueCollection that = (CreditValueCollection) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, deltas);
    }

    @Override
    public String toString() {
        return "CreditValueCollection{" +
                "values=" + values +
                ", deltas=" + deltas +
                '}';
    }
}
