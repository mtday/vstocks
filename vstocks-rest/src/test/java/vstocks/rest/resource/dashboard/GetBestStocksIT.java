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
        Results<StockPriceChange> results = new Results<StockPriceChange>().setPage(Page.from(1, 20, 0, 0));
        StockPriceChangeService stockPriceChangeService = mock(StockPriceChangeService.class);
        when(stockPriceChangeService.getAll(any(), any())).thenReturn(results);
        when(getServiceFactory().getStockPriceChangeService()).thenReturn(stockPriceChangeService);

        Response response = target("/dashboard/stocks/best").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"page\":{\"page\":1,\"size\":20,\"totalPages\":0,\"firstRow\":null,\"lastRow\":null,"
                + "\"totalRows\":0},\"results\":[]}", json);

        Results<StockPriceChange> fetched = convert(json, new StockPriceChangeResultsTypeRef());
        assertEquals(results, fetched);
    }

    @Test
    public void testGetBestOnePage() {
        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        StockPriceChange stockPriceChange1 = new StockPriceChange()
                .setBatch(1)
                .setMarket(TWITTER)
                .setSymbol("symbol1")
                .setTimestamp(timestamp)
                .setPrice(10)
                .setChange(1)
                .setPercent(10f);
        StockPriceChange stockPriceChange2 = new StockPriceChange()
                .setBatch(1)
                .setMarket(TWITTER)
                .setSymbol("symbol2")
                .setTimestamp(timestamp)
                .setPrice(20)
                .setChange(1)
                .setPercent(5f);
        StockPriceChange stockPriceChange3 = new StockPriceChange()
                .setBatch(1)
                .setMarket(TWITTER)
                .setSymbol("symbol3")
                .setTimestamp(timestamp)
                .setPrice(10)
                .setChange(0)
                .setPercent(0f);

        Results<StockPriceChange> results = new Results<StockPriceChange>().setPage(Page.from(1, 20, 3, 3))
                .setResults(asList(stockPriceChange1, stockPriceChange2, stockPriceChange3));
        StockPriceChangeService stockPriceChangeService = mock(StockPriceChangeService.class);
        when(stockPriceChangeService.getAll(any(), any())).thenReturn(results);
        when(getServiceFactory().getStockPriceChangeService()).thenReturn(stockPriceChangeService);

        Response response = target("/dashboard/stocks/best").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String change1json = "{\"batch\":1,\"market\":\"Twitter\",\"symbol\":\"symbol1\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"price\":10,\"change\":1,\"percent\":10.0}";
        String change2json = "{\"batch\":1,\"market\":\"Twitter\",\"symbol\":\"symbol2\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"price\":20,\"change\":1,\"percent\":5.0}";
        String change3json = "{\"batch\":1,\"market\":\"Twitter\",\"symbol\":\"symbol3\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"price\":10,\"change\":0,\"percent\":0.0}";
        String expectedJson = "{\"page\":{\"page\":1,\"size\":20,\"totalPages\":1,\"firstRow\":1,\"lastRow\":3,"
                + "\"totalRows\":3},\"results\":[" + change1json + "," + change2json + "," + change3json + "]}";
        assertEquals(expectedJson, json);

        Results<StockPriceChange> fetched = convert(json, new StockPriceChangeResultsTypeRef());
        assertEquals(Page.from(1, 20, 3, 3), fetched.getPage());
        assertTrue(fetched.getResults().contains(stockPriceChange1));
        assertTrue(fetched.getResults().contains(stockPriceChange2));
        assertTrue(fetched.getResults().contains(stockPriceChange3));
    }
}
