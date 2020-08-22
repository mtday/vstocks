package vstocks.db.jdbc;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.ServiceFactoryImpl;

import static org.junit.Assert.assertNotNull;

public class JdbcDBFactoryIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private ServiceFactoryImpl dbFactory;

    @Before
    public void setup() {
        dbFactory = new ServiceFactoryImpl(dataSourceExternalResource.get());
    }

    @Test
    public void test() {
        assertNotNull(dbFactory.getActivityLogDB());
        assertNotNull(dbFactory.getOwnedStockDB());
        assertNotNull(dbFactory.getPortfolioValueDB());
        assertNotNull(dbFactory.getPortfolioValueRankDB());
        assertNotNull(dbFactory.getPortfolioValueSummaryDB());
        assertNotNull(dbFactory.getPricedStockDB());
        assertNotNull(dbFactory.getPricedUserStockDB());
        assertNotNull(dbFactory.getStockDB());
        assertNotNull(dbFactory.getStockPriceDB());
        assertNotNull(dbFactory.getTransactionSummaryDB());
        assertNotNull(dbFactory.getUserAchievementDB());
        assertNotNull(dbFactory.getUserCountDB());
        assertNotNull(dbFactory.getUserCreditsDB());
        assertNotNull(dbFactory.getUserDB());
        assertNotNull(dbFactory.getUserStockDB());
    }
}
