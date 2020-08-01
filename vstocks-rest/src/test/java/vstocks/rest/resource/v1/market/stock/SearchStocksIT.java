package vstocks.rest.resource.v1.market.stock;

import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.PricedStock;
import vstocks.model.Stock;
import vstocks.rest.ResourceTest;
import vstocks.service.remote.RemoteStockService;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class SearchStocksIT extends ResourceTest {
    private final PricedStock stock1 = new PricedStock().setMarket(TWITTER).setName("name1").setSymbol("symbol1").setTimestamp(Instant.now()).setPrice(1);
    private final PricedStock stock2 = new PricedStock().setMarket(TWITTER).setName("name2").setSymbol("symbol2").setTimestamp(Instant.now()).setPrice(2);
    private final PricedStock stock3 = new PricedStock().setMarket(TWITTER).setName("name3").setSymbol("symbol3").setTimestamp(Instant.now()).setPrice(3);

    @Test
    public void testSearchStocksMarketMissing() {
        Response response = target("/v1/market/missing/stocks/search").queryParam("q", "searchterm").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());
    }

    @Test
    public void testSearchStocksNoSearchParam() {
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("searchterm"), eq(20))).thenReturn(emptyList());
        when(getRemoteStockServiceFactory().getForMarket(TWITTER)).thenReturn(remoteStockService);

        Response response = target("/v1/market/twitter/stocks/search").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        List<Stock> results = response.readEntity(new StockListGenericType());
        assertTrue(results.isEmpty());
    }


    @Test
    public void testSearchStocksNone() {
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("searchterm"), eq(20))).thenReturn(emptyList());
        when(getRemoteStockServiceFactory().getForMarket(TWITTER)).thenReturn(remoteStockService);

        Response response = target("/v1/market/twitter/stocks/search").queryParam("q", "searchterm").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        List<Stock> results = response.readEntity(new StockListGenericType());
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchStocksSome() {
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("searchterm"), eq(20))).thenReturn(asList(stock1, stock2, stock3));
        when(getRemoteStockServiceFactory().getForMarket(TWITTER)).thenReturn(remoteStockService);

        Response response = target("/v1/market/twitter/stocks/search").queryParam("q", "searchterm").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        List<PricedStock> results = response.readEntity(new PricedStockListGenericType());
        assertEquals(3, results.size());
        assertTrue(results.contains(stock1));
        assertTrue(results.contains(stock2));
        assertTrue(results.contains(stock3));
    }
}
