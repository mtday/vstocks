package vstocks.db;

public interface DBFactory {
    ActivityLogDB getActivityLogDB();

    OwnedStockDB getOwnedStockDB();

    PortfolioValueDB getPortfolioValueDB();

    PortfolioValueRankDB getPortfolioValueRankDB();

    PortfolioValueSummaryDB getPortfolioValueSummaryDB();

    PricedStockDB getPricedStockDB();

    PricedUserStockDB getPricedUserStockDB();

    StockDB getStockDB();

    StockPriceDB getStockPriceDB();

    UserAchievementDB getUserAchievementDB();

    UserCreditsDB getUserCreditsDB();

    UserDB getUserDB();

    UserStockDB getUserStockDB();
}
