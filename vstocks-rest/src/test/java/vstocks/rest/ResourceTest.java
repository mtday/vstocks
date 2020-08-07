package vstocks.rest;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.ClassRule;
import org.pac4j.core.profile.CommonProfile;
import vstocks.db.DBFactory;
import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.service.remote.RemoteStockServiceFactory;

import javax.ws.rs.core.GenericType;
import java.util.List;
import java.util.logging.LogManager;

import static org.mockito.Mockito.mock;

public abstract class ResourceTest extends JerseyTest {
    static {
        // Disable the java.util.logging used by the embedded webserver used in the tests
        LogManager.getLogManager().reset();
    }

    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private DBFactory dbFactory;
    private RemoteStockServiceFactory remoteStockServiceFactory;

    @Override
    protected ResourceConfig configure() {
        dbFactory = mock(DBFactory.class);
        remoteStockServiceFactory = mock(RemoteStockServiceFactory.class);

        Environment environment = new Environment()
                .setDBFactory(dbFactory)
                .setRemoteStockServiceFactory(remoteStockServiceFactory)
                .setIncludeSecurity(false) // disables Pac4j, we include a simple profile below
                .setIncludeBackgroundTasks(false);

        Application application = new Application(environment);
        application.register(new CommonProfileValueParamProvider(getCommonProfile()));
        return application;
    }

    public User getUser() {
        return new User()
                .setEmail("user@domain.com")
                .setUsername("username")
                .setDisplayName("Display Name");
    }

    public CommonProfile getCommonProfile() {
        User user = getUser();
        CommonProfile commonProfile = new CommonProfile();
        commonProfile.setClientName("TwitterClient");
        commonProfile.setId("12345");
        commonProfile.addAttribute("email", user.getEmail());
        commonProfile.addAttribute("username", user.getUsername());
        commonProfile.addAttribute("display_name", user.getDisplayName());
        return commonProfile;
    }

    public DBFactory getDBFactory() {
        return dbFactory;
    }

    public RemoteStockServiceFactory getRemoteStockServiceFactory() {
        return remoteStockServiceFactory;
    }

    public static class MarketListGenericType extends GenericType<List<Market>> {}
    public static class PricedStockResultsGenericType extends GenericType<Results<PricedStock>> {}
    public static class PricedStockListGenericType extends GenericType<List<PricedStock>> {}
}
