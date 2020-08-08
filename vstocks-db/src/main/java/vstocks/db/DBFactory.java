package vstocks.db;

public interface DBFactory {
    ActivityLogDB getActivityLogDB();

    StockPriceDB getStockPriceDB();

    StockDB getStockDB();

    PricedStockDB getPricedStockDB();

    UserAchievementDB getUserAchievementDB();

    UserBalanceDB getUserBalanceDB();

    UserDB getUserDB();

    UserStockDB getUserStockDB();

    PricedUserStockDB getPricedUserStockDB();
}
