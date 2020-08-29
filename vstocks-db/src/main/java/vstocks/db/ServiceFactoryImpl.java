package vstocks.db;

import vstocks.db.portfolio.*;
import vstocks.db.system.*;

import javax.sql.DataSource;

public class ServiceFactoryImpl implements ServiceFactory {
    private final ActivityLogService activityLogService;
    private final OwnedStockService ownedStockService;
    private final PricedStockService pricedStockService;
    private final PricedUserStockService pricedUserStockService;
    private final StockService stockService;
    private final StockPriceService stockPriceService;
    private final StockPriceChangeService stockPriceChangeService;
    private final UserAchievementService userAchievementService;
    private final UserCreditsService userCreditsService;
    private final UserService userService;
    private final UserStockService userStockService;

    // portfolio services

    private final CreditRankService creditRankService;
    private final MarketRankService marketRankService;
    private final MarketTotalRankService marketTotalRankService;
    private final TotalRankService totalRankService;
    private final PortfolioValueService portfolioValueService;

    // system services

    private final ActiveUserCountService activeUserCountService;
    private final TotalUserCountService totalUserCountService;
    private final ActiveTransactionCountService activeTransactionCountService;
    private final TotalTransactionCountService totalTransactionCountService;
    private final OverallCreditValueService overallCreditValueService;
    private final OverallMarketValueService overallMarketValueService;
    private final OverallMarketTotalValueService overallMarketTotalValueService;
    private final OverallTotalValueService overallTotalValueService;

    public ServiceFactoryImpl(DataSource dataSource) {
        this.activityLogService = new ActivityLogServiceImpl(dataSource);
        this.ownedStockService = new OwnedStockServiceImpl(dataSource);
        this.pricedStockService = new PricedStockServiceImpl(dataSource);
        this.pricedUserStockService = new PricedUserStockServiceImpl(dataSource);
        this.stockService = new StockServiceImpl(dataSource);
        this.stockPriceService = new StockPriceServiceImpl(dataSource);
        this.stockPriceChangeService = new StockPriceChangeServiceImpl(dataSource);
        this.userAchievementService = new UserAchievementServiceImpl(dataSource);
        this.userCreditsService = new UserCreditsServiceImpl(dataSource);
        this.userService = new UserServiceImpl(dataSource);
        this.userStockService = new UserStockServiceImpl(dataSource);

        this.creditRankService = new CreditRankServiceImpl(dataSource);
        this.marketRankService = new MarketRankServiceImpl(dataSource);
        this.marketTotalRankService = new MarketTotalRankServiceImpl(dataSource);
        this.totalRankService = new TotalRankServiceImpl(dataSource);
        this.portfolioValueService = new PortfolioValueServiceImpl(dataSource);

        this.activeUserCountService = new ActiveUserCountServiceImpl(dataSource);
        this.totalUserCountService = new TotalUserCountServiceImpl(dataSource);
        this.activeTransactionCountService = new ActiveTransactionCountServiceImpl(dataSource);
        this.totalTransactionCountService = new TotalTransactionCountServiceImpl(dataSource);
        this.overallCreditValueService = new OverallCreditValueServiceImpl(dataSource);
        this.overallMarketValueService = new OverallMarketValueServiceImpl(dataSource);
        this.overallMarketTotalValueService = new OverallMarketTotalValueServiceImpl(dataSource);
        this.overallTotalValueService = new OverallTotalValueServiceImpl(dataSource);
    }

    @Override
    public ActivityLogService getActivityLogService() {
        return activityLogService;
    }

    @Override
    public OwnedStockService getOwnedStockService() {
        return ownedStockService;
    }

    @Override
    public PricedStockService getPricedStockService() {
        return pricedStockService;
    }

    @Override
    public PricedUserStockService getPricedUserStockService() {
        return pricedUserStockService;
    }

    @Override
    public StockService getStockService() {
        return stockService;
    }

    @Override
    public StockPriceService getStockPriceService() {
        return stockPriceService;
    }

    @Override
    public StockPriceChangeService getStockPriceChangeService() {
        return stockPriceChangeService;
    }

    @Override
    public UserAchievementService getUserAchievementService() {
        return userAchievementService;
    }

    @Override
    public UserCreditsService getUserCreditsService() {
        return userCreditsService;
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
    public CreditRankService getCreditRankService() {
        return creditRankService;
    }

    @Override
    public MarketRankService getMarketRankService() {
        return marketRankService;
    }

    @Override
    public MarketTotalRankService getMarketTotalRankService() {
        return marketTotalRankService;
    }

    @Override
    public TotalRankService getTotalRankService() {
        return totalRankService;
    }

    @Override
    public PortfolioValueService getPortfolioValueService() {
        return portfolioValueService;
    }

    @Override
    public ActiveUserCountService getActiveUserCountService() {
        return activeUserCountService;
    }

    @Override
    public TotalUserCountService getTotalUserCountService() {
        return totalUserCountService;
    }

    @Override
    public ActiveTransactionCountService getActiveTransactionCountService() {
        return activeTransactionCountService;
    }

    @Override
    public TotalTransactionCountService getTotalTransactionCountService() {
        return totalTransactionCountService;
    }

    @Override
    public OverallCreditValueService getOverallCreditValueService() {
        return overallCreditValueService;
    }

    @Override
    public OverallMarketValueService getOverallMarketValueService() {
        return overallMarketValueService;
    }

    @Override
    public OverallMarketTotalValueService getOverallMarketTotalValueService() {
        return overallMarketTotalValueService;
    }

    @Override
    public OverallTotalValueService getOverallTotalValueService() {
        return overallTotalValueService;
    }
}
