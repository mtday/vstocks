package vstocks.db;

public interface DBFactory {
    PortfolioValueDB getPortfolioValueDB();

    ActivityLogDB getActivityLogDB();

    StockPriceDB getStockPriceDB();

    StockDB getStockDB();

    PricedStockDB getPricedStockDB();

    UserAchievementDB getUserAchievementDB();

    UserCreditsDB getUserCreditsDB();

    UserDB getUserDB();

    UserStockDB getUserStockDB();

    PricedUserStockDB getPricedUserStockDB();
}
