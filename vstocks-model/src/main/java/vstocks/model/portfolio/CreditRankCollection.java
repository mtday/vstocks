package vstocks.model.portfolio;

import vstocks.model.Delta;

import java.util.List;
import java.util.Objects;

public class CreditRankCollection {
    private List<CreditRank> ranks;
    private List<Delta> deltas;

    public CreditRankCollection() {
    }

    public List<CreditRank> getRanks() {
        return ranks;
    }

    public CreditRankCollection setRanks(List<CreditRank> ranks) {
        this.ranks = ranks;
        return this;
    }

    public List<Delta> getDeltas() {
        return deltas;
    }

    public CreditRankCollection setDeltas(List<Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreditRankCollection that = (CreditRankCollection) o;
        return Objects.equals(ranks, that.ranks) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ranks, deltas);
    }

    @Override
    public String toString() {
        return "CreditRankCollection{" +
                "ranks=" + ranks +
                ", deltas=" + deltas +
                '}';
    }
}
