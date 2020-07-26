package vstocks.rest.resource.v1.market;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.Market;
import vstocks.rest.Application;
import vstocks.rest.DataSourceExternalResource;
import vstocks.service.ServiceFactory;
import vstocks.service.jdbc.JdbcServiceFactory;

import javax.inject.Inject;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;

public class GetMarketIT extends JerseyTest {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private final ServiceFactory serviceFactory = new JdbcServiceFactory(dataSourceExternalResource.get());

    @Override
    protected Application configure() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(GetMarket.class);
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(serviceFactory).to(ServiceFactory.class);
            }
        });
        return new Application();
    }

    @Test
    public void testMarketMissing() {
        Response response = target("/v1/market/missing").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market with id missing not found", error.getMessage());
    }

    @Test
    @Inject
    public void testMarketExists() {
        Market market = new Market().setId("id").setName("name");
        serviceFactory.getMarketService().add(market);

        Response response = target("/v1/market/id").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Market fetched = response.readEntity(Market.class);
        assertEquals(market.getId(), fetched.getId());
        assertEquals(market.getName(), fetched.getName());
    }
}
