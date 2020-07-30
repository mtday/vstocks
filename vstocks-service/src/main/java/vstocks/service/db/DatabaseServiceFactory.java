package vstocks.service.db;

public interface DatabaseServiceFactory {
    ActivityLogService getActivityLogService();

    StockPriceService getStockPriceService();

    StockService getStockService();

    UserBalanceService getUserBalanceService();

    UserService getUserService();

    UserStockService getUserStockService();
}
