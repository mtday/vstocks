package vstocks.service;

public interface ServiceFactory {
    ActivityLogService getActivityLogService();

    MarketService getMarketService();

    StockPriceService getStockPriceService();

    StockService getStockService();

    UserBalanceService getUserBalanceService();

    UserService getUserService();

    UserStockService getUserStockService();
}
