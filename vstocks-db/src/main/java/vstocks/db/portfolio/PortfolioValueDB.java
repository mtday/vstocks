package vstocks.db.portfolio;

import vstocks.db.BaseDB;
import vstocks.model.portfolio.PortfolioValue;

import java.sql.Connection;
import java.util.Optional;

class PortfolioValueDB extends BaseDB {
    private final PortfolioValueSummaryDB portfolioValueSummaryDB = new PortfolioValueSummaryDB();
    private final CreditRankDB creditRankDB = new CreditRankDB();
    private final MarketRankDB marketRankDB = new MarketRankDB();
    private final MarketTotalRankDB marketTotalRankDB = new MarketTotalRankDB();
    private final TotalRankDB totalRankDB = new TotalRankDB();

    public Optional<PortfolioValue> getForUser(Connection connection, String userId) {
        return portfolioValueSummaryDB.getForUser(connection, userId).map(summary -> {
            PortfolioValue portfolioValue = new PortfolioValue();
            portfolioValue.setSummary(summary);
            portfolioValue.setCreditRanks(creditRankDB.getLatest(connection, userId));
            portfolioValue.setMarketRanks(marketRankDB.getLatest(connection, userId));
            portfolioValue.setMarketTotalRanks(marketTotalRankDB.getLatest(connection, userId));
            portfolioValue.setTotalRanks(totalRankDB.getLatest(connection, userId));
            return portfolioValue;
        });
    }
}
