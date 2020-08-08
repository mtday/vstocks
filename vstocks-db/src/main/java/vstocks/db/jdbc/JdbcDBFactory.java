package vstocks.db.jdbc;

import vstocks.db.*;

import javax.sql.DataSource;

public class JdbcDBFactory implements DBFactory {
    private final ActivityLogDB activityLogDB;
    private final StockPriceDB stockPriceDB;
    private final StockDB stockDB;
    private final PricedStockDB pricedStockDB;
    private final UserAchievementDB userAchievementDB;
    private final UserBalanceDB userBalanceDB;
    private final UserDB userDB;
    private final UserStockDB userStockDB;
    private final PricedUserStockDB pricedUserStockDB;

    public JdbcDBFactory(DataSource dataSource) {
        this.activityLogDB = new JdbcActivityLogDB(dataSource);
        this.stockPriceDB = new JdbcStockPriceDB(dataSource);
        this.stockDB = new JdbcStockDB(dataSource);
        this.pricedStockDB = new JdbcPricedStockDB(dataSource);
        this.userAchievementDB = new JdbcUserAchievementDB(dataSource);
        this.userBalanceDB = new JdbcUserBalanceDB(dataSource);
        this.userDB = new JdbcUserDB(dataSource);
        this.userStockDB = new JdbcUserStockDB(dataSource);
        this.pricedUserStockDB = new JdbcPricedUserStockDB(dataSource);
    }

    @Override
    public ActivityLogDB getActivityLogDB() {
        return activityLogDB;
    }

    @Override
    public StockPriceDB getStockPriceDB() {
        return stockPriceDB;
    }

    @Override
    public StockDB getStockDB() {
        return stockDB;
    }

    @Override
    public PricedStockDB getPricedStockDB() {
        return pricedStockDB;
    }

    @Override
    public UserAchievementDB getUserAchievementDB() {
        return userAchievementDB;
    }

    @Override
    public UserBalanceDB getUserBalanceDB() {
        return userBalanceDB;
    }

    @Override
    public UserDB getUserDB() {
        return userDB;
    }

    @Override
    public UserStockDB getUserStockDB() {
        return userStockDB;
    }

    @Override
    public PricedUserStockDB getPricedUserStockDB() {
        return pricedUserStockDB;
    }
}
