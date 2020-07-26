package vstocks.rest.resource.v1.market.stock;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.rest.Application;
import vstocks.rest.DataSourceExternalResource;
import vstocks.rest.resource.v1.market.GetAllMarkets;
import vstocks.service.ServiceFactory;
import vstocks.service.jdbc.JdbcServiceFactory;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetAllStocksForMarketIT extends JerseyTest {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private final ServiceFactory serviceFactory = new JdbcServiceFactory(dataSourceExternalResource.get());

    private static class StockResultsGenericType extends GenericType<Results<Stock>> {}

    private final Market market1 = new Market().setId("id1").setName("market1");
    private final Market market2 = new Market().setId("id2").setName("market2");
    private final Stock stock1 = new Stock().setId("id1").setMarketId(market1.getId()).setName("name1").setSymbol("symbol1");
    private final Stock stock2 = new Stock().setId("id2").setMarketId(market1.getId()).setName("name2").setSymbol("symbol2");
    private final Stock stock3 = new Stock().setId("id3").setMarketId(market1.getId()).setName("name3").setSymbol("symbol3");

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

    @Before
    public void setup() {
        serviceFactory.getMarketService().add(market1);
        serviceFactory.getMarketService().add(market2);
        serviceFactory.getStockService().add(stock1);
        serviceFactory.getStockService().add(stock2);
        serviceFactory.getStockService().add(stock3);
    }

    @After
    public void cleanup() {
        serviceFactory.getStockService().delete(market1.getId(), stock1.getId());
        serviceFactory.getStockService().delete(market1.getId(), stock2.getId());
        serviceFactory.getStockService().delete(market1.getId(), stock3.getId());
        serviceFactory.getMarketService().delete(market1.getId());
        serviceFactory.getMarketService().delete(market2.getId());
    }

    @Test
    public void testAllStocksForMarketMarketMissing() {
        Response response = target("/v1/market/missing/stocks").request().get();

        // Doesn't attempt to verify whether the market exists since most of the time it will and no need to
        // waste performance on checking market existence.
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Stock> results = response.readEntity(new StockResultsGenericType());
        assertEquals(1, results.getPage().getPage());
        assertEquals(25, results.getPage().getSize());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testAllStocksForMarketNone() {
        Response response = target("/v1/market/id2/stocks").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Stock> results = response.readEntity(new StockResultsGenericType());
        assertEquals(1, results.getPage().getPage());
        assertEquals(25, results.getPage().getSize());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testAllMarketsOnePage() {
        Response response = target("/v1/market/id1/stocks").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Stock> results = response.readEntity(new StockResultsGenericType());
        assertEquals(1, results.getPage().getPage());
        assertEquals(25, results.getPage().getSize());
        assertEquals(3, results.getTotal());
        assertTrue(results.getResults().contains(stock1));
        assertTrue(results.getResults().contains(stock2));
        assertTrue(results.getResults().contains(stock3));
    }

    @Test
    public void testAllMarketsSpecificPage() {
        Response response = target("/v1/market/id1/stocks").queryParam("pageNum", 2).queryParam("pageSize", 1).request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Stock> results = response.readEntity(new StockResultsGenericType());
        assertEquals(2, results.getPage().getPage());
        assertEquals(1, results.getPage().getSize());
        assertEquals(3, results.getTotal());
        assertTrue(results.getResults().contains(stock2));
    }
}
