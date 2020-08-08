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
        assertNotNull(dbFactory.getStockPriceDB());
        assertNotNull(dbFactory.getStockDB());
        assertNotNull(dbFactory.getPricedStockDB());
        assertNotNull(dbFactory.getUserAchievementDB());
        assertNotNull(dbFactory.getUserBalanceDB());
        assertNotNull(dbFactory.getUserDB());
        assertNotNull(dbFactory.getUserStockDB());
        assertNotNull(dbFactory.getPricedUserStockDB());
    }
}
