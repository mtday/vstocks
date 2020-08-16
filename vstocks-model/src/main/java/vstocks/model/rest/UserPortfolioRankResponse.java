package vstocks.model.rest;

import vstocks.model.PortfolioValueRank;

import java.util.List;

public class UserPortfolioRankResponse {
    private PortfolioValueRank currentRank;
    private List<PortfolioValueRank> historicalRanks;

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
}
