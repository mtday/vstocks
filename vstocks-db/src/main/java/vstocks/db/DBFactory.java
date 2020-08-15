package vstocks.db;

public interface DBFactory {
    ActivityLogDB getActivityLogDB();

    OwnedStockDB getOwnedStockDB();

    PortfolioValueDB getPortfolioValueDB();

    PortfolioValueRankDB getPortfolioValueRankDB();

    PricedStockDB getPricedStockDB();

    PricedUserStockDB getPricedUserStockDB();

    StockDB getStockDB();

    StockPriceDB getStockPriceDB();

    UserAchievementDB getUserAchievementDB();

    UserCreditsDB getUserCreditsDB();

    UserDB getUserDB();

    UserStockDB getUserStockDB();
}
