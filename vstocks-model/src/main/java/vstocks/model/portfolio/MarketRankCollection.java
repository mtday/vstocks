package vstocks.model.portfolio;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MarketRankCollection {
    private List<MarketRank> ranks;
    private Map<DeltaInterval, Delta> deltas;

    public MarketRankCollection() {
    }

    public List<MarketRank> getRanks() {
        return ranks;
    }

    public MarketRankCollection setRanks(List<MarketRank> ranks) {
        this.ranks = ranks;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public MarketRankCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketRankCollection that = (MarketRankCollection) o;
        return Objects.equals(ranks, that.ranks) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ranks, deltas);
    }

    @Override
    public String toString() {
        return "PortfolioMarketRankCollection{" +
                "ranks=" + ranks +
                ", deltas=" + deltas +
                '}';
    }
}
