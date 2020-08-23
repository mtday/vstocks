package vstocks.db;

import vstocks.db.portfolio.*;
import vstocks.db.system.ActiveUserCountService;
import vstocks.db.system.ActiveUserCountServiceImpl;
import vstocks.db.system.TotalUserCountService;
import vstocks.db.system.TotalUserCountServiceImpl;

import javax.sql.DataSource;

public class ServiceFactoryImpl implements ServiceFactory {
    private final ActivityLogService activityLogService;
    private final OwnedStockService ownedStockService;
    private final PricedStockService pricedStockService;
    private final PricedUserStockService pricedUserStockService;
    private final StockService stockService;
    private final StockPriceService stockPriceService;
    private final UserAchievementService userAchievementService;
    private final UserCreditsService userCreditsService;
    private final UserService userService;
    private final UserStockService userStockService;

    // portfolio services

    private final CreditRankService creditRankService;
    private final CreditValueService creditValueService;
    private final MarketRankService marketRankService;
    private final MarketValueService marketValueService;
    private final MarketTotalRankService marketTotalRankService;
    private final MarketTotalValueService marketTotalValueService;
    private final TotalRankService totalRankService;
    private final TotalValueService totalValueService;

    // system services

    private final ActiveUserCountService activeUserCountService;
    private final TotalUserCountService totalUserCountService;

    public ServiceFactoryImpl(DataSource dataSource) {
        this.activityLogService = new ActivityLogServiceImpl(dataSource);
        this.ownedStockService = new OwnedStockServiceImpl(dataSource);
        this.pricedStockService = new PricedStockServiceImpl(dataSource);
        this.pricedUserStockService = new PricedUserStockServiceImpl(dataSource);
        this.stockService = new StockServiceImpl(dataSource);
        this.stockPriceService = new StockPriceServiceImpl(dataSource);
        this.userAchievementService = new UserAchievementServiceImpl(dataSource);
        this.userCreditsService = new UserCreditsServiceImpl(dataSource);
        this.userService = new UserServiceImpl(dataSource);
        this.userStockService = new UserStockServiceImpl(dataSource);

        this.creditRankService = new CreditRankServiceImpl(dataSource);
        this.creditValueService = new CreditValueServiceImpl(dataSource);
        this.marketRankService = new MarketRankServiceImpl(dataSource);
        this.marketValueService = new MarketValueServiceImpl(dataSource);
        this.marketTotalRankService = new MarketTotalRankServiceImpl(dataSource);
        this.marketTotalValueService = new MarketTotalValueServiceImpl(dataSource);
        this.totalRankService = new TotalRankServiceImpl(dataSource);
        this.totalValueService = new TotalValueServiceImpl(dataSource);

        this.activeUserCountService = new ActiveUserCountServiceImpl(dataSource);
        this.totalUserCountService = new TotalUserCountServiceImpl(dataSource);
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
    public CreditValueService getCreditValueService() {
        return creditValueService;
    }

    @Override
    public MarketRankService getMarketRankService() {
        return marketRankService;
    }

    @Override
    public MarketValueService getMarketValueService() {
        return marketValueService;
    }

    @Override
    public MarketTotalRankService getMarketTotalRankService() {
        return marketTotalRankService;
    }

    @Override
    public MarketTotalValueService getMarketTotalValueService() {
        return marketTotalValueService;
    }

    @Override
    public TotalRankService getTotalRankService() {
        return totalRankService;
    }

    @Override
    public TotalValueService getTotalValueService() {
        return totalValueService;
    }

    @Override
    public ActiveUserCountService getActiveUserCountService() {
        return activeUserCountService;
    }

    @Override
    public TotalUserCountService getTotalUserCountService() {
        return totalUserCountService;
    }
}
