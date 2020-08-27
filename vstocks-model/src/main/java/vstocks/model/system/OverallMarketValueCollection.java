package vstocks.model.system;

import vstocks.model.Delta;
import vstocks.model.Market;

import java.util.List;
import java.util.Objects;

public class OverallMarketValueCollection {
    private Market market;
    private List<OverallMarketValue> values;
    private List<Delta> deltas;

    public OverallMarketValueCollection() {
    }

    public Market getMarket() {
        return market;
    }

    public OverallMarketValueCollection setMarket(Market market) {
        this.market = market;
        return this;
    }

    public List<OverallMarketValue> getValues() {
        return values;
    }

    public OverallMarketValueCollection setValues(List<OverallMarketValue> values) {
        this.values = values;
        return this;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public OverallMarketValueCollection setDeltas(List<Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OverallMarketValueCollection that = (OverallMarketValueCollection) o;
        return market == that.market &&
                Objects.equals(values, that.values) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market, values, deltas);
    }

    @Override
    public String toString() {
        return "OverallMarketValueCollection{" +
                "market=" + market +
                ", values=" + values +
                ", deltas=" + deltas +
                '}';
    }
}
