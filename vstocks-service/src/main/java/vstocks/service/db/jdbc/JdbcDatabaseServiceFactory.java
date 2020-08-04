package vstocks.service.db.jdbc;

import vstocks.service.db.*;

import javax.sql.DataSource;

public class JdbcDatabaseServiceFactory implements DatabaseServiceFactory {
    private final ActivityLogService activityLogService;
    private final StockPriceService stockPriceService;
    private final StockService stockService;
    private final PricedStockService pricedStockService;
    private final UserBalanceService userBalanceService;
    private final UserService userService;
    private final UserStockService userStockService;
    private final PricedUserStockService pricedUserStockService;

    public JdbcDatabaseServiceFactory(DataSource dataSource) {
        this.activityLogService = new JdbcActivityLogService(dataSource);
        this.stockPriceService = new JdbcStockPriceService(dataSource);
        this.stockService = new JdbcStockService(dataSource);
        this.pricedStockService = new JdbcPricedStockService(dataSource);
        this.userBalanceService = new JdbcUserBalanceService(dataSource);
        this.userService = new JdbcUserService(dataSource);
        this.userStockService = new JdbcUserStockService(dataSource);
        this.pricedUserStockService = new JdbcPricedUserStockService(dataSource);
    }

    @Override
    public ActivityLogService getActivityLogService() {
        return activityLogService;
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
    public PricedStockService getPricedStockService() {
        return pricedStockService;
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

    @Override
    public PricedUserStockService getPricedUserStockService() {
        return pricedUserStockService;
    }
}
