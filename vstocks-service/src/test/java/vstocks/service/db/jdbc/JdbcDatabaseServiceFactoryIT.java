package vstocks.service.db.jdbc;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.service.db.DataSourceExternalResource;

import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;

public class JdbcDatabaseServiceFactoryIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private JdbcDatabaseServiceFactory databaseServiceFactory;

    @Before
    public void setup() throws SQLException {
        databaseServiceFactory = new JdbcDatabaseServiceFactory(dataSourceExternalResource.get());
    }

    @Test
    public void test() {
        assertNotNull(databaseServiceFactory.getActivityLogService());
        assertNotNull(databaseServiceFactory.getMarketService());
        assertNotNull(databaseServiceFactory.getStockPriceService());
        assertNotNull(databaseServiceFactory.getStockService());
        assertNotNull(databaseServiceFactory.getUserBalanceService());
        assertNotNull(databaseServiceFactory.getUserService());
        assertNotNull(databaseServiceFactory.getUserStockService());
    }
}
