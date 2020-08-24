package vstocks.db;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ServiceFactoryImplIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private ServiceFactoryImpl serviceFactory;

    @Before
    public void setup() {
        serviceFactory = new ServiceFactoryImpl(dataSourceExternalResource.get());
    }

    @Test
    public void test() {
        assertNotNull(serviceFactory.getActivityLogService());
        assertNotNull(serviceFactory.getOwnedStockService());
        assertNotNull(serviceFactory.getPricedStockService());
        assertNotNull(serviceFactory.getPricedUserStockService());
        assertNotNull(serviceFactory.getStockService());
        assertNotNull(serviceFactory.getStockPriceService());
        assertNotNull(serviceFactory.getUserAchievementService());
        assertNotNull(serviceFactory.getUserCreditsService());
        assertNotNull(serviceFactory.getUserService());
        assertNotNull(serviceFactory.getUserStockService());

        // portfolio services

        assertNotNull(serviceFactory.getCreditRankService());
        assertNotNull(serviceFactory.getMarketRankService());
        assertNotNull(serviceFactory.getMarketTotalRankService());
        assertNotNull(serviceFactory.getTotalRankService());

        // system services

        assertNotNull(serviceFactory.getActiveUserCountService());
        assertNotNull(serviceFactory.getTotalUserCountService());
        assertNotNull(serviceFactory.getActiveTransactionCountService());
        assertNotNull(serviceFactory.getTotalTransactionCountService());
    }
}