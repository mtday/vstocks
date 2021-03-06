package vstocks.model.portfolio;

import vstocks.model.Delta;

import java.util.List;
import java.util.Objects;

public class MarketTotalRankCollection {
    private List<MarketTotalRank> ranks;
    private List<Delta> deltas;

    public MarketTotalRankCollection() {
    }

    public List<MarketTotalRank> getRanks() {
        return ranks;
    }

    public MarketTotalRankCollection setRanks(List<MarketTotalRank> ranks) {
        this.ranks = ranks;
        return this;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public MarketTotalRankCollection setDeltas(List<Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarketTotalRankCollection that = (MarketTotalRankCollection) o;
        return Objects.equals(ranks, that.ranks) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ranks, deltas);
    }

    @Override
    public String toString() {
        return "MarketTotalRankCollection{" +
                "ranks=" + ranks +
                ", deltas=" + deltas +
                '}';
    }
}
