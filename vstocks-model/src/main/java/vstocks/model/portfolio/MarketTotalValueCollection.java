package vstocks.model.portfolio;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MarketTotalValueCollection {
    private List<MarketTotalValue> values;
    private Map<DeltaInterval, Delta> deltas;

    public MarketTotalValueCollection() {
    }

    public List<MarketTotalValue> getValues() {
        return values;
    }

    public MarketTotalValueCollection setValues(List<MarketTotalValue> values) {
        this.values = values;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public MarketTotalValueCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketTotalValueCollection that = (MarketTotalValueCollection) o;
        return Objects.equals(values, that.values) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values, deltas);
    }

    @Override
    public String toString() {
        return "MarketTotalValueCollection{" +
                "values=" + values +
                ", deltas=" + deltas +
                '}';
    }
}
