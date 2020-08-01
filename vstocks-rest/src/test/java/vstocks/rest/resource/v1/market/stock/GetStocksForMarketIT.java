package vstocks.rest.resource.v1.market.stock;

import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.rest.ResourceTest;
import vstocks.service.db.StockService;

import javax.ws.rs.core.Response;

import static java.util.Arrays.asList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class GetStocksForMarketIT extends ResourceTest {
    private final Stock stock1 = new Stock().setMarket(TWITTER).setName("name1").setSymbol("symbol1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setName("name2").setSymbol("symbol2");
    private final Stock stock3 = new Stock().setMarket(TWITTER).setName("name3").setSymbol("symbol3");

    @Test
    public void testGetForMarketMissing() {
        Response response = target("/v1/market/missing/stocks").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());
    }

    @Test
    public void testGetForMarketsNone() {
        StockService stockService = mock(StockService.class);
        when(stockService.getForMarket(eq(TWITTER), any())).thenReturn(new Results<>());
        when(getDatabaseServiceFactory().getStockService()).thenReturn(stockService);

        Response response = target("/v1/market/twitter/stocks").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Stock> results = response.readEntity(new StockResultsGenericType());
        assertEquals(1, results.getPage().getPage());
        assertEquals(25, results.getPage().getSize());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForMarketsOnePage() {
        Results<Stock> results = new Results<Stock>().setPage(new Page()).setTotal(3).setResults(asList(stock1, stock2, stock3));
        StockService stockService = mock(StockService.class);
        when(stockService.getForMarket(eq(TWITTER), any())).thenReturn(results);
        when(getDatabaseServiceFactory().getStockService()).thenReturn(stockService);

        Response response = target("/v1/market/twitter/stocks").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Stock> fetched = response.readEntity(new StockResultsGenericType());
        assertEquals(1, fetched.getPage().getPage());
        assertEquals(25, fetched.getPage().getSize());
        assertEquals(3, fetched.getTotal());
        assertTrue(fetched.getResults().contains(stock1));
        assertTrue(fetched.getResults().contains(stock2));
        assertTrue(fetched.getResults().contains(stock3));
    }
}
