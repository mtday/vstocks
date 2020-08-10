package vstocks.db;

public interface DBFactory {
    ActivityLogDB getActivityLogDB();

    StockPriceDB getStockPriceDB();

    StockDB getStockDB();

    PortfolioValueDB getPortfolioValueDB();

    PortfolioValueRankDB getPortfolioValueRankDB();

    PricedStockDB getPricedStockDB();

    UserAchievementDB getUserAchievementDB();

    UserCreditsDB getUserCreditsDB();

    UserDB getUserDB();

    UserStockDB getUserStockDB();

    PricedUserStockDB getPricedUserStockDB();
}
