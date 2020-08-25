package vstocks.rest.resource.dashboard;

import org.junit.Test;
import vstocks.db.StockPriceChangeService;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.StockPriceChange;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class GetBestStocksIT extends ResourceTest {
    @Test
    public void testGetBestNone() {
        StockPriceChangeService stockPriceChangeService = mock(StockPriceChangeService.class);
        when(stockPriceChangeService.getAll(any(), any())).thenReturn(new Results<>());
        when(getServiceFactory().getStockPriceChangeService()).thenReturn(stockPriceChangeService);

        Response response = target("/dashboard/stocks/best").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<StockPriceChange> results = response.readEntity(new StockPriceChangeResultsGenericType());
        assertEquals(1, results.getPage().getPage());
        assertEquals(25, results.getPage().getSize());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetBestOnePage() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPriceChange stockPriceChange1 = new StockPriceChange()
                .setMarket(TWITTER)
                .setSymbol("symbol1")
                .setTimestamp(now)
                .setPrice(10)
                .setChange(1)
                .setPercent(10f);
        StockPriceChange stockPriceChange2 = new StockPriceChange()
                .setMarket(TWITTER)
                .setSymbol("symbol2")
                .setTimestamp(now)
                .setPrice(20)
                .setChange(1)
                .setPercent(5f);
        StockPriceChange stockPriceChange3 = new StockPriceChange()
                .setMarket(TWITTER)
                .setSymbol("symbol3")
                .setTimestamp(now)
                .setPrice(10)
                .setChange(0)
                .setPercent(0f);

        Results<StockPriceChange> results = new Results<StockPriceChange>().setPage(new Page()).setTotal(3)
                .setResults(asList(stockPriceChange1, stockPriceChange2, stockPriceChange3));
        StockPriceChangeService stockPriceChangeService = mock(StockPriceChangeService.class);
        when(stockPriceChangeService.getAll(any(), any())).thenReturn(results);
        when(getServiceFactory().getStockPriceChangeService()).thenReturn(stockPriceChangeService);

        Response response = target("/dashboard/stocks/best").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<StockPriceChange> fetched = response.readEntity(new StockPriceChangeResultsGenericType());
        assertEquals(1, fetched.getPage().getPage());
        assertEquals(25, fetched.getPage().getSize());
        assertEquals(3, fetched.getTotal());
        assertTrue(fetched.getResults().contains(stockPriceChange1));
        assertTrue(fetched.getResults().contains(stockPriceChange2));
        assertTrue(fetched.getResults().contains(stockPriceChange3));
    }
}
