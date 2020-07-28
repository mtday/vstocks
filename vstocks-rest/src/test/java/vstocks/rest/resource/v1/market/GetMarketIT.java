package vstocks.rest.resource.v1.market;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.Market;
import vstocks.rest.Application;
import vstocks.rest.DataSourceExternalResource;
import vstocks.service.ServiceFactory;
import vstocks.service.jdbc.JdbcServiceFactory;
import vstocks.service.jdbc.table.MarketTable;

import javax.ws.rs.core.Response;
import java.sql.Connection;
import java.sql.SQLException;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;

public class GetMarketIT extends JerseyTest {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private ServiceFactory serviceFactory;

    @Override
    protected ResourceConfig configure() {
        serviceFactory = new JdbcServiceFactory(dataSourceExternalResource.get());
        return new Application(dataSourceExternalResource.get(), false);
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            new MarketTable().truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testMarketMissing() {
        Response response = target("/v1/market/missing").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());
    }

    @Test
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
