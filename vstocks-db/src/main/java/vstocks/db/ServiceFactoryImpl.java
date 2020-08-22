package vstocks.db;

import javax.sql.DataSource;

public class ServiceFactoryImpl implements ServiceFactory {
    private final ActivityLogService activityLogDB;
    private final OwnedStockService ownedStockDB;
    private final PricedStockService pricedStockDB;
    private final PricedUserStockService pricedUserStockDB;
    private final StockService stockDB;
    private final StockPriceService stockPriceDB;
    private final UserAchievementService userAchievementDB;
    private final UserCreditsService userCreditsDB;
    private final UserService userDB;
    private final UserStockService userStockDB;

    public ServiceFactoryImpl(DataSource dataSource) {
        this.activityLogDB = new ActivityLogServiceImpl(dataSource);
        this.ownedStockDB = new OwnedStockServiceImpl(dataSource);
        this.pricedStockDB = new PricedStockServiceImpl(dataSource);
        this.pricedUserStockDB = new PricedUserStockServiceImpl(dataSource);
        this.stockDB = new StockServiceImpl(dataSource);
        this.stockPriceDB = new StockPriceServiceImpl(dataSource);
        this.userAchievementDB = new UserAchievementServiceImpl(dataSource);
        this.userCreditsDB = new UserCreditsServiceImpl(dataSource);
        this.userDB = new UserServiceImpl(dataSource);
        this.userStockDB = new UserStockServiceImpl(dataSource);
    }

    @Override
    public ActivityLogService getActivityLogDB() {
        return activityLogDB;
    }

    @Override
    public OwnedStockService getOwnedStockDB() {
        return ownedStockDB;
    }

    @Override
    public PricedStockService getPricedStockDB() {
        return pricedStockDB;
    }

    @Override
    public PricedUserStockService getPricedUserStockDB() {
        return pricedUserStockDB;
    }

    @Override
    public StockService getStockDB() {
        return stockDB;
    }

    @Override
    public StockPriceService getStockPriceDB() {
        return stockPriceDB;
    }

    @Override
    public UserAchievementService getUserAchievementDB() {
        return userAchievementDB;
    }

    @Override
    public UserCreditsService getUserCreditsDB() {
        return userCreditsDB;
    }

    @Override
    public UserService getUserDB() {
        return userDB;
    }

    @Override
    public UserStockService getUserStockDB() {
        return userStockDB;
    }
}
