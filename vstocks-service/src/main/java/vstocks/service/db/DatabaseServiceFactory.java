package vstocks.service.db;

public interface DatabaseServiceFactory {
    ActivityLogService getActivityLogService();

    StockPriceService getStockPriceService();

    StockService getStockService();

    PricedStockService getPricedStockService();

    UserBalanceService getUserBalanceService();

    UserService getUserService();

    UserStockService getUserStockService();

    PricedUserStockService getPricedUserStockService();
}
