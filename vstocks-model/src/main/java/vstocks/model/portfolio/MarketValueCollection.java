package vstocks.model.portfolio;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MarketValueCollection {
    private List<MarketValue> values;
    private Map<DeltaInterval, Delta> deltas;

    public MarketValueCollection() {
    }

    public List<MarketValue> getValues() {
        return values;
    }

    public MarketValueCollection setValues(List<MarketValue> values) {
        this.values = values;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public MarketValueCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketValueCollection that = (MarketValueCollection) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, deltas);
    }

    @Override
    public String toString() {
        return "MarketValueCollection{" +
                "values=" + values +
                ", deltas=" + deltas +
                '}';
    }
}
