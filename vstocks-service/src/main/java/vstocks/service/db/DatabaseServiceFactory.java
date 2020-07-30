package vstocks.service.db;

public interface DatabaseServiceFactory {
    ActivityLogService getActivityLogService();

    MarketService getMarketService();

    StockPriceService getStockPriceService();

    StockService getStockService();

    UserBalanceService getUserBalanceService();

    UserService getUserService();

    UserStockService getUserStockService();
}
