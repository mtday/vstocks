package vstocks.db.jdbc;

import vstocks.db.*;

import javax.sql.DataSource;

public class JdbcDBFactory implements DBFactory {
    private final ActivityLogDB activityLogDB;
    private final OwnedStockDB ownedStockDB;
    private final PortfolioValueDB portfolioValueDB;
    private final PortfolioValueRankDB portfolioValueRankDB;
    private final PortfolioValueSummaryDB portfolioValueSummaryDB;
    private final PricedStockDB pricedStockDB;
    private final PricedUserStockDB pricedUserStockDB;
    private final StockDB stockDB;
    private final StockPriceDB stockPriceDB;
    private final TransactionSummaryDB transactionSummaryDB;
    private final UserAchievementDB userAchievementDB;
    private final UserCountDB userCountDB;
    private final UserCreditsDB userCreditsDB;
    private final UserDB userDB;
    private final UserStockDB userStockDB;

    public JdbcDBFactory(DataSource dataSource) {
        this.activityLogDB = new JdbcActivityLogDB(dataSource);
        this.ownedStockDB = new JdbcOwnedStockDB(dataSource);
        this.portfolioValueDB = new JdbcPortfolioValueDB(dataSource);
        this.portfolioValueRankDB = new JdbcPortfolioValueRankDB(dataSource);
        this.portfolioValueSummaryDB = new JdbcPortfolioValueSummaryDB(dataSource);
        this.pricedStockDB = new JdbcPricedStockDB(dataSource);
        this.pricedUserStockDB = new JdbcPricedUserStockDB(dataSource);
        this.stockDB = new JdbcStockDB(dataSource);
        this.stockPriceDB = new JdbcStockPriceDB(dataSource);
        this.transactionSummaryDB = new JdbcTransactionSummaryDB(dataSource);
        this.userAchievementDB = new JdbcUserAchievementDB(dataSource);
        this.userCountDB = new JdbcUserCountDB(dataSource);
        this.userCreditsDB = new JdbcUserCreditsDB(dataSource);
        this.userDB = new JdbcUserDB(dataSource);
        this.userStockDB = new JdbcUserStockDB(dataSource);
    }

    @Override
    public ActivityLogDB getActivityLogDB() {
        return activityLogDB;
    }

    @Override
    public OwnedStockDB getOwnedStockDB() {
        return ownedStockDB;
    }

    @Override
    public PortfolioValueDB getPortfolioValueDB() {
        return portfolioValueDB;
    }

    @Override
    public PortfolioValueRankDB getPortfolioValueRankDB() {
        return portfolioValueRankDB;
    }

    @Override
    public PortfolioValueSummaryDB getPortfolioValueSummaryDB() {
        return portfolioValueSummaryDB;
    }

    @Override
    public PricedStockDB getPricedStockDB() {
        return pricedStockDB;
    }

    @Override
    public PricedUserStockDB getPricedUserStockDB() {
        return pricedUserStockDB;
    }

    @Override
    public StockDB getStockDB() {
        return stockDB;
    }

    @Override
    public StockPriceDB getStockPriceDB() {
        return stockPriceDB;
    }

    @Override
    public TransactionSummaryDB getTransactionSummaryDB() {
        return transactionSummaryDB;
    }

    @Override
    public UserAchievementDB getUserAchievementDB() {
        return userAchievementDB;
    }

    @Override
    public UserCountDB getUserCountDB() {
        return userCountDB;
    }

    @Override
    public UserCreditsDB getUserCreditsDB() {
        return userCreditsDB;
    }

    @Override
    public UserDB getUserDB() {
        return userDB;
    }

    @Override
    public UserStockDB getUserStockDB() {
        return userStockDB;
    }
}
