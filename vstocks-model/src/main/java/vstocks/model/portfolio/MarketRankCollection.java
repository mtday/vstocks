package vstocks.model.portfolio;

import vstocks.model.Delta;
import vstocks.model.Market;

import java.util.List;
import java.util.Objects;

public class MarketRankCollection {
    private Market market;
    private List<MarketRank> ranks;
    private List<Delta> deltas;

    public MarketRankCollection() {
    }

    public Market getMarket() {
        return market;
    }

    public MarketRankCollection setMarket(Market market) {
        this.market = market;
        return this;
    }

    public List<MarketRank> getRanks() {
        return ranks;
    }

    public MarketRankCollection setRanks(List<MarketRank> ranks) {
        this.ranks = ranks;
        return this;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public MarketRankCollection setDeltas(List<Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketRankCollection that = (MarketRankCollection) o;
        return market == that.market &&
                Objects.equals(ranks, that.ranks) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(market.name(), ranks, deltas);
    }

    @Override
    public String toString() {
        return "MarketRankCollection{" +
                "market=" + market +
                ", ranks=" + ranks +
                ", deltas=" + deltas +
                '}';
    }
}
