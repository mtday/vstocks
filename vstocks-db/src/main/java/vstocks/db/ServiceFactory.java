package vstocks.db;

import vstocks.db.portfolio.*;
import vstocks.db.system.ActiveTransactionCountService;
import vstocks.db.system.ActiveUserCountService;
import vstocks.db.system.TotalTransactionCountService;
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
    MarketRankService getMarketRankService();
    MarketTotalRankService getMarketTotalRankService();
    TotalRankService getTotalRankService();

    // system services

    ActiveUserCountService getActiveUserCountService();
    TotalUserCountService getTotalUserCountService();
    ActiveTransactionCountService getActiveTransactionCountService();
    TotalTransactionCountService getTotalTransactionCountService();
}
