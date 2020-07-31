package vstocks.rest.resource.v1.market.stock;

import org.junit.Before;
import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.Market.TWITTER;

public class GetStocksForMarketIT extends ResourceTest {
    private final Stock stock1 = new Stock().setMarket(TWITTER).setName("name1").setSymbol("symbol1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setName("name2").setSymbol("symbol2");
    private final Stock stock3 = new Stock().setMarket(TWITTER).setName("name3").setSymbol("symbol3");

    @Before
    public void setup() {
        getDatabaseServiceFactory().getStockService().add(stock1);
        getDatabaseServiceFactory().getStockService().add(stock2);
        getDatabaseServiceFactory().getStockService().add(stock3);
    }

    @Test
    public void testAllStocksForMarketMarketMissing() {
        Response response = target("/v1/market/missing/stocks").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());
    }

    @Test
    public void testAllStocksForMarketNone() {
        Response response = target("/v1/market/youtube/stocks").request().get();

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
        Response response = target("/v1/market/twitter/stocks").request().get();

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
}
