package vstocks.rest.resource.v1.market;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.rest.Application;
import vstocks.rest.DataSourceExternalResource;
import vstocks.service.ServiceFactory;
import vstocks.service.jdbc.JdbcServiceFactory;
import vstocks.service.jdbc.table.MarketTable;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetAllMarketsIT extends JerseyTest {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private final ServiceFactory serviceFactory = new JdbcServiceFactory(dataSourceExternalResource.get());

    private static class MarketResultsGenericType extends GenericType<Results<Market>> {}

    private final Market market1 = new Market().setId("id1").setName("name1");
    private final Market market2 = new Market().setId("id2").setName("name2");
    private final Market market3 = new Market().setId("id3").setName("name3");

    @Override
    protected Application configure() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(GetAllMarkets.class);
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(serviceFactory).to(ServiceFactory.class);
            }
        });
        return new Application();
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            new MarketTable().truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testAllMarketsNone() {
        Response response = target("/v1/markets").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Market> results = response.readEntity(new MarketResultsGenericType());
        assertEquals(1, results.getPage().getPage());
        assertEquals(25, results.getPage().getSize());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testAllMarketsOnePage() {
        serviceFactory.getMarketService().add(market1);
        serviceFactory.getMarketService().add(market2);
        serviceFactory.getMarketService().add(market3);

        Response response = target("/v1/markets").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Market> results = response.readEntity(new MarketResultsGenericType());
        assertEquals(1, results.getPage().getPage());
        assertEquals(25, results.getPage().getSize());
        assertEquals(3, results.getTotal());
        assertTrue(results.getResults().contains(market1));
        assertTrue(results.getResults().contains(market2));
        assertTrue(results.getResults().contains(market3));
    }

    @Test
    public void testAllMarketsSpecificPage() {
        serviceFactory.getMarketService().add(market1);
        serviceFactory.getMarketService().add(market2);
        serviceFactory.getMarketService().add(market3);

        Response response = target("/v1/markets").queryParam("pageNum", 2).queryParam("pageSize", 1).request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Market> results = response.readEntity(new MarketResultsGenericType());
        assertEquals(2, results.getPage().getPage());
        assertEquals(1, results.getPage().getSize());
        assertEquals(3, results.getTotal());
        assertTrue(results.getResults().contains(market2));
    }
}
