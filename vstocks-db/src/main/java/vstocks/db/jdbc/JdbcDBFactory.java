package vstocks.db.jdbc;

import vstocks.db.*;

import javax.sql.DataSource;

public class JdbcDBFactory implements DBFactory {
    private final ActivityLogDB activityLogDb;
    private final StockPriceDB stockPriceDb;
    private final StockDB stockDb;
    private final PricedStockDB pricedStockDb;
    private final UserBalanceDB userBalanceDb;
    private final UserDB userDb;
    private final UserStockDB userStockDb;
    private final PricedUserStockDB pricedUserStockDb;

    public JdbcDBFactory(DataSource dataSource) {
        this.activityLogDb = new JdbcActivityLogDB(dataSource);
        this.stockPriceDb = new JdbcStockPriceDB(dataSource);
        this.stockDb = new JdbcStockDB(dataSource);
        this.pricedStockDb = new JdbcPricedStockDB(dataSource);
        this.userBalanceDb = new JdbcUserBalanceDB(dataSource);
        this.userDb = new JdbcUserDB(dataSource);
        this.userStockDb = new JdbcUserStockDB(dataSource);
        this.pricedUserStockDb = new JdbcPricedUserStockDB(dataSource);
    }

    @Override
    public ActivityLogDB getActivityLogDB() {
        return activityLogDb;
    }

    @Override
    public StockPriceDB getStockPriceDB() {
        return stockPriceDb;
    }

    @Override
    public StockDB getStockDB() {
        return stockDb;
    }

    @Override
    public PricedStockDB getPricedStockDB() {
        return pricedStockDb;
    }

    @Override
    public UserBalanceDB getUserBalanceDB() {
        return userBalanceDb;
    }

    @Override
    public UserDB getUserDB() {
        return userDb;
    }

    @Override
    public UserStockDB getUserStockDB() {
        return userStockDb;
    }

    @Override
    public PricedUserStockDB getPricedUserStockDB() {
        return pricedUserStockDb;
    }
}
