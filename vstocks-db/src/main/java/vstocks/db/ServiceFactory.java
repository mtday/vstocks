package vstocks.db;

public interface ServiceFactory {
    ActivityLogService getActivityLogDB();

    OwnedStockService getOwnedStockDB();

    PricedStockService getPricedStockDB();

    PricedUserStockService getPricedUserStockDB();

    StockService getStockDB();

    StockPriceService getStockPriceDB();

    UserAchievementService getUserAchievementDB();

    UserCreditsService getUserCreditsDB();

    UserService getUserDB();

    UserStockService getUserStockDB();
}
