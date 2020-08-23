package vstocks.db;

import vstocks.db.portfolio.*;
import vstocks.db.system.ActiveUserCountService;
import vstocks.db.system.TotalUserCountService;

public interface ServiceFactory {
    ActivityLogService getActivityLogService();
    OwnedStockService getOwnedStockService();
    PricedStockService getPricedStockService();
    PricedUserStockService getPricedUserStockService();
    StockService getStockService();
    StockPriceService getStockPriceService();
    UserAchievementService getUserAchievementService();
    UserCreditsService getUserCreditsService();
    UserService getUserService();
    UserStockService getUserStockService();

    // portfolio services

    CreditRankService getCreditRankService();
    CreditValueService getCreditValueService();
    MarketRankService getMarketRankService();
    MarketValueService getMarketValueService();
    MarketTotalRankService getMarketTotalRankService();
    MarketTotalValueService getMarketTotalValueService();
    TotalRankService getTotalRankService();
    TotalValueService getTotalValueService();

    // system services

    ActiveUserCountService getActiveUserCountService();
    TotalUserCountService getTotalUserCountService();
}
