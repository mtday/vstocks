package vstocks.db.jdbc;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;

import static org.junit.Assert.assertNotNull;

public class JdbcDBFactoryIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private JdbcDBFactory dbFactory;

    @Before
    public void setup() {
        dbFactory = new JdbcDBFactory(dataSourceExternalResource.get());
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
        assertNotNull(dbFactory.getUserAchievementDB());
        assertNotNull(dbFactory.getUserCreditsDB());
        assertNotNull(dbFactory.getUserDB());
        assertNotNull(dbFactory.getUserStockDB());
    }
}
