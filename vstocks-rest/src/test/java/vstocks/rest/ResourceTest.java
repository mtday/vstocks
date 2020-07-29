package vstocks.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.ClassRule;
import org.pac4j.core.profile.CommonProfile;
import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.service.ServiceFactory;
import vstocks.service.jdbc.JdbcServiceFactory;
import vstocks.service.jdbc.table.*;

import javax.ws.rs.core.GenericType;
import java.sql.Connection;
import java.sql.SQLException;

public class ResourceTest extends JerseyTest {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private ServiceFactory serviceFactory;

    @Override
    protected ResourceConfig configure() {
        serviceFactory = new JdbcServiceFactory(dataSourceExternalResource.get());

        Application application = new Application(dataSourceExternalResource.get(), false, false);
        application.register(new CommonProfileValueParamProvider(getCommonProfile()));
        return application;
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            new ActivityLogTable().truncate(connection);
            new MarketTable().truncate(connection);
            new StockPriceTable().truncate(connection);
            new StockTable().truncate(connection);
            new UserBalanceTable().truncate(connection);
            new UserStockTable().truncate(connection);
            new UserTable().truncate(connection);
            connection.commit();
        }
    }

    public CommonProfile getCommonProfile() {
        CommonProfile commonProfile = new CommonProfile();
        commonProfile.setClientName("TwitterClient");
        commonProfile.setId("12345");
        commonProfile.addAttribute("username", "username");
        commonProfile.addAttribute("display_name", "Display Name");
        return commonProfile;
    }

    public ServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public static class MarketResultsGenericType extends GenericType<Results<Market>> {}
    public static class StockResultsGenericType extends GenericType<Results<Stock>> {}
}
