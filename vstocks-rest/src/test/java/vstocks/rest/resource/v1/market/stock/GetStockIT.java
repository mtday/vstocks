package vstocks.rest.resource.v1.market.stock;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.Market;
import vstocks.model.Stock;
import vstocks.rest.Application;
import vstocks.rest.DataSourceExternalResource;
import vstocks.service.ServiceFactory;
import vstocks.service.jdbc.JdbcServiceFactory;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;

public class GetStockIT extends JerseyTest {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private final ServiceFactory serviceFactory = new JdbcServiceFactory(dataSourceExternalResource.get());

    private final Market market = new Market().setId("marketId").setName("market");
    private final Stock stock = new Stock().setId("stockId").setMarketId(market.getId()).setName("name").setSymbol("symbol");

    @Override
    protected Application configure() {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(GetStock.class);
        resourceConfig.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(serviceFactory).to(ServiceFactory.class);
            }
        });
        return new Application();
    }

    @Before
    public void setup() {
        serviceFactory.getMarketService().add(market);
        serviceFactory.getStockService().add(stock);
    }

    @After
    public void cleanup() {
        serviceFactory.getStockService().delete(market.getId(), stock.getId());
        serviceFactory.getMarketService().delete(market.getId());
    }

    @Test
    public void testMarketMissing() {
        Response response = target("/v1/market/missing/stock/stockId").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock missing/stockId not found", error.getMessage());
    }

    @Test
    public void testStockMissing() {
        Response response = target("/v1/market/marketId/stock/missing").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock marketId/missing not found", error.getMessage());
    }

    @Test
    public void testMarketExists() {
        Response response = target("/v1/market/marketId/stock/stockId").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Stock fetched = response.readEntity(Stock.class);
        assertEquals(stock.getId(), fetched.getId());
        assertEquals(stock.getMarketId(), fetched.getMarketId());
        assertEquals(stock.getName(), fetched.getName());
        assertEquals(stock.getSymbol(), fetched.getSymbol());
    }
}
