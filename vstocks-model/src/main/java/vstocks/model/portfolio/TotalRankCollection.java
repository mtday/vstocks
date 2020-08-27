package vstocks.model.portfolio;

import vstocks.model.Delta;

import java.util.List;
import java.util.Objects;

public class TotalRankCollection {
    private List<TotalRank> ranks;
    private List<Delta> deltas;

    public TotalRankCollection() {
    }

    public List<TotalRank> getRanks() {
        return ranks;
    }

    public TotalRankCollection setRanks(List<TotalRank> ranks) {
        this.ranks = ranks;
        return this;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public TotalRankCollection setDeltas(List<Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TotalRankCollection that = (TotalRankCollection) o;
        return Objects.equals(ranks, that.ranks) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ranks, deltas);
    }

    @Override
    public String toString() {
        return "TotalRankCollection{" +
                "ranks=" + ranks +
                ", deltas=" + deltas +
                '}';
    }
}
