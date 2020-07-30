package vstocks.rest.resource.v1.market.stock;

import org.junit.Before;
import org.junit.Test;
import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetStocksForMarketIT extends ResourceTest {
    private final Market market1 = new Market().setId("id1").setName("market1");
    private final Market market2 = new Market().setId("id2").setName("market2");
    private final Stock stock1 = new Stock().setId("id1").setMarketId(market1.getId()).setName("name1").setSymbol("symbol1");
    private final Stock stock2 = new Stock().setId("id2").setMarketId(market1.getId()).setName("name2").setSymbol("symbol2");
    private final Stock stock3 = new Stock().setId("id3").setMarketId(market1.getId()).setName("name3").setSymbol("symbol3");

    @Before
    public void setup() {
        getDatabaseServiceFactory().getMarketService().add(market1);
        getDatabaseServiceFactory().getMarketService().add(market2);
        getDatabaseServiceFactory().getStockService().add(stock1);
        getDatabaseServiceFactory().getStockService().add(stock2);
        getDatabaseServiceFactory().getStockService().add(stock3);
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
