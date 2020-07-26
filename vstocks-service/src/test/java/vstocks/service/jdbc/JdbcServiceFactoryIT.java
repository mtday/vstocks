package vstocks.service.jdbc;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.service.DataSourceExternalResource;

import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;

public class JdbcServiceFactoryIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private JdbcServiceFactory serviceFactory;

    @Before
    public void setup() throws SQLException {
        serviceFactory = new JdbcServiceFactory(dataSourceExternalResource.get());
    }

    @Test
    public void test() {
        assertNotNull(serviceFactory.getActivityLogService());
        assertNotNull(serviceFactory.getMarketService());
        assertNotNull(serviceFactory.getStockPriceService());
        assertNotNull(serviceFactory.getStockService());
        assertNotNull(serviceFactory.getUserBalanceService());
        assertNotNull(serviceFactory.getUserService());
        assertNotNull(serviceFactory.getUserStockService());
    }
}
