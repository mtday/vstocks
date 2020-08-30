package vstocks.rest.resource.market.stock;

import org.junit.Test;
import vstocks.db.StockPriceChangeService;
import vstocks.model.ErrorResponse;
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
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class GetBestStocksForMarketIT extends ResourceTest {
    @Test
    public void testGetBestForMarketMissing() {
        Response response = target("/market/missing/stocks/best").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":404,\"message\":\"Market missing not found\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), errorResponse.getStatus());
        assertEquals("Market missing not found", errorResponse.getMessage());
    }

    @Test
    public void testGetBestForMarketsNone() {
        Results<StockPriceChange> results = new Results<StockPriceChange>().setPage(Page.from(1, 20, 0, 0));
        StockPriceChangeService stockPriceChangeService = mock(StockPriceChangeService.class);
        when(stockPriceChangeService.getForMarket(eq(TWITTER), any(), any())).thenReturn(results);
        when(getServiceFactory().getStockPriceChangeService()).thenReturn(stockPriceChangeService);

        Response response = target("/market/twitter/stocks/best").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"page\":{\"page\":1,\"size\":20,\"totalPages\":0,\"firstRow\":null,\"lastRow\":null,"
                + "\"totalRows\":0},\"results\":[]}", json);

        Results<StockPriceChange> fetched = convert(json, new StockPriceChangeResultsTypeRef());
        assertEquals(results, fetched);
    }

    @Test
    public void testGetBestForMarketsOnePage() {
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
        when(stockPriceChangeService.getForMarket(eq(TWITTER), any(), any())).thenReturn(results);
        when(getServiceFactory().getStockPriceChangeService()).thenReturn(stockPriceChangeService);

        Response response = target("/market/twitter/stocks/best").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String change1json = "{\"batch\":1,\"market\":\"Twitter\",\"symbol\":\"symbol1\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"price\":10,\"change\":1,\"percent\":10.0}";
        String change2json = "{\"batch\":1,\"market\":\"Twitter\",\"symbol\":\"symbol2\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"price\":20,\"change\":1,\"percent\":5.0}";
        String change3json = "{\"batch\":1,\"market\":\"Twitter\",\"symbol\":\"symbol3\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"price\":10,\"change\":0,\"percent\":0.0}";
        String expected = "{\"page\":{\"page\":1,\"size\":20,\"totalPages\":1,\"firstRow\":1,\"lastRow\":3,"
                + "\"totalRows\":3},\"results\":[" + change1json + "," + change2json + "," + change3json + "]}";
        assertEquals(expected, json);

        Results<StockPriceChange> fetched = convert(json, new StockPriceChangeResultsTypeRef());
        assertEquals(Page.from(1, 20, 3, 3), results.getPage());
        assertTrue(fetched.getResults().contains(stockPriceChange1));
        assertTrue(fetched.getResults().contains(stockPriceChange2));
        assertTrue(fetched.getResults().contains(stockPriceChange3));
    }
}
