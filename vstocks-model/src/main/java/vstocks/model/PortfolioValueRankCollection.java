package vstocks.model;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PortfolioValueRankCollection {
    private List<PortfolioValueRank> ranks;
    private Map<DeltaInterval, Delta> deltas;

    public PortfolioValueRankCollection() {
    }

    public List<PortfolioValueRank> getRanks() {
        return ranks;
    }

    public PortfolioValueRankCollection setRanks(List<PortfolioValueRank> ranks) {
        this.ranks = ranks;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public PortfolioValueRankCollection setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortfolioValueRankCollection that = (PortfolioValueRankCollection) o;
        return Objects.equals(ranks, that.ranks) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ranks, deltas);
    }

    @Override
    public String toString() {
        return "PortfolioValueRankCollection{" +
                "ranks=" + ranks +
                ", deltas=" + deltas +
                '}';
    }
}
