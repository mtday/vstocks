package vstocks.model.system;

import vstocks.model.Delta;
import vstocks.model.Market;

import java.util.List;
import java.util.Objects;

public class TotalMarketTransactionCountCollection {
    private Market market;
    private List<TotalMarketTransactionCount> counts;
    private List<Delta> deltas;

    public TotalMarketTransactionCountCollection() {
    }

    public Market getMarket() {
        return market;
    }

    public TotalMarketTransactionCountCollection setMarket(Market market) {
        this.market = market;
        return this;
    }

    public List<TotalMarketTransactionCount> getCounts() {
        return counts;
    }

    public TotalMarketTransactionCountCollection setCounts(List<TotalMarketTransactionCount> counts) {
        this.counts = counts;
        return this;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public TotalMarketTransactionCountCollection setDeltas(List<Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalMarketTransactionCountCollection that = (TotalMarketTransactionCountCollection) o;
        return market == that.market &&
                Objects.equals(counts, that.counts) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market, counts, deltas);
    }

    @Override
    public String toString() {
        return "TotalMarketTransactionCountCollection{" +
                "market=" + market +
                ", counts=" + counts +
                ", deltas=" + deltas +
                '}';
    }
}
