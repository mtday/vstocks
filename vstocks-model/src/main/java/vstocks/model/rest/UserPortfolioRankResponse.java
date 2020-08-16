package vstocks.model.rest;

import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.PortfolioValueRank;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserPortfolioRankResponse {
    private PortfolioValueRank currentRank;
    private List<PortfolioValueRank> historicalRanks;
    private Map<DeltaInterval, Delta> deltas;

    public UserPortfolioRankResponse() {
    }

    public PortfolioValueRank getCurrentRank() {
        return currentRank;
    }

    public UserPortfolioRankResponse setCurrentRank(PortfolioValueRank currentRank) {
        this.currentRank = currentRank;
        return this;
    }

    public List<PortfolioValueRank> getHistoricalRanks() {
        return historicalRanks;
    }

    public UserPortfolioRankResponse setHistoricalRanks(List<PortfolioValueRank> historicalRanks) {
        this.historicalRanks = historicalRanks;
        return this;
    }

    public Map<DeltaInterval, Delta> getDeltas() {
        return deltas;
    }

    public UserPortfolioRankResponse setDeltas(Map<DeltaInterval, Delta> deltas) {
        this.deltas = deltas;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPortfolioRankResponse that = (UserPortfolioRankResponse) o;
        return Objects.equals(currentRank, that.currentRank) &&
                Objects.equals(historicalRanks, that.historicalRanks) &&
                Objects.equals(deltas, that.deltas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentRank, historicalRanks, deltas);
    }

    @Override
    public String toString() {
        return "UserPortfolioRankResponse{" +
                "currentRank=" + currentRank +
                ", historicalRanks=" + historicalRanks +
                ", deltas=" + deltas +
                '}';
    }
}
