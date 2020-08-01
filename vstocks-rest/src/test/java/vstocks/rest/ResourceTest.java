package vstocks.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.ClassRule;
import org.pac4j.core.profile.CommonProfile;
import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.service.db.DatabaseServiceFactory;
import vstocks.service.remote.RemoteStockServiceFactory;

import javax.ws.rs.core.GenericType;
import java.util.List;

import static org.mockito.Mockito.mock;

public abstract class ResourceTest extends JerseyTest {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private DatabaseServiceFactory databaseServiceFactory;
    private RemoteStockServiceFactory remoteStockServiceFactory;

    @Override
    protected ResourceConfig configure() {
        databaseServiceFactory = mock(DatabaseServiceFactory.class);
        remoteStockServiceFactory = mock(RemoteStockServiceFactory.class);

        Environment environment = new Environment()
                .setDatabaseServiceFactory(databaseServiceFactory)
                .setRemoteStockServiceFactory(remoteStockServiceFactory)
                .setIncludeSecurity(false) // disableds Pac4j, we include a simple profile below
                .setIncludeBackgroundTasks(false);

        Application application = new Application(environment);
        application.register(new CommonProfileValueParamProvider(getCommonProfile()));
        return application;
    }

    public CommonProfile getCommonProfile() {
        CommonProfile commonProfile = new CommonProfile();
        commonProfile.setClientName("TwitterClient");
        commonProfile.setId("12345");
        commonProfile.addAttribute("username", "username");
        commonProfile.addAttribute("display_name", "Display Name");
        return commonProfile;
    }

    public DatabaseServiceFactory getDatabaseServiceFactory() {
        return databaseServiceFactory;
    }

    public RemoteStockServiceFactory getRemoteStockServiceFactory() {
        return remoteStockServiceFactory;
    }

    public static class MarketListGenericType extends GenericType<List<Market>> {}
    public static class StockResultsGenericType extends GenericType<Results<Stock>> {}
    public static class StockListGenericType extends GenericType<List<Stock>> {}
    public static class PricedStockListGenericType extends GenericType<List<PricedStock>> {}
}
