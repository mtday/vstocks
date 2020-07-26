package vstocks.service.jdbc;

import vstocks.service.*;

import javax.sql.DataSource;

public class JdbcServiceFactory implements ServiceFactory {
    private final ActivityLogService activityLogService;
    private final MarketService marketService;
    private final StockPriceService stockPriceService;
    private final StockService stockService;
    private final UserBalanceService userBalanceService;
    private final UserService userService;
    private final UserStockService userStockService;

    public JdbcServiceFactory(DataSource dataSource) {
        this.activityLogService = new JdbcActivityLogService(dataSource);
        this.marketService = new JdbcMarketService(dataSource);
        this.stockPriceService = new JdbcStockPriceService(dataSource);
        this.stockService = new JdbcStockService(dataSource);
        this.userBalanceService = new JdbcUserBalanceService(dataSource);
        this.userService = new JdbcUserService(dataSource);
        this.userStockService = new JdbcUserStockService(dataSource);
    }

    @Override
    public ActivityLogService getActivityLogService() {
        return activityLogService;
    }

    @Override
    public MarketService getMarketService() {
        return marketService;
    }

    @Override
    public StockPriceService getStockPriceService() {
        return stockPriceService;
    }

    @Override
    public StockService getStockService() {
        return stockService;
    }

    @Override
    public UserBalanceService getUserBalanceService() {
        return userBalanceService;
    }

    @Override
    public UserService getUserService() {
        return userService;
    }

    @Override
    public UserStockService getUserStockService() {
        return userStockService;
    }
}
