package vstocks.rest.resource.v1.market.stock;

import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.PricedStock;
import vstocks.rest.ResourceTest;
import vstocks.service.remote.RemoteStockService;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class SearchStocksIT extends ResourceTest {
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
        Response response = target("/v1/market/twitter/stocks/search").request().get();

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), error.getStatus());
        assertEquals("Missing required 'q' query parameter", error.getMessage());
    }

    @Test
    public void testSearchStocksNone() {
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("searchterm"), eq(20))).thenReturn(emptyList());
        when(getRemoteStockServiceFactory().getForMarket(TWITTER)).thenReturn(remoteStockService);

        Response response = target("/v1/market/twitter/stocks/search").queryParam("q", "searchterm").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertTrue(response.readEntity(new PricedStockListGenericType()).isEmpty());
    }

    @Test
    public void testSearchStocksSome() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock1 = new PricedStock().setMarket(TWITTER).setSymbol("symbol1").setName("name1").setTimestamp(now).setPrice(10);
        PricedStock pricedStock2 = new PricedStock().setMarket(TWITTER).setSymbol("symbol2").setName("name2").setTimestamp(now).setPrice(11);
        PricedStock pricedStock3 = new PricedStock().setMarket(TWITTER).setSymbol("symbol3").setName("name3").setTimestamp(now).setPrice(12);

        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("searchterm"), eq(20))).thenReturn(asList(pricedStock1, pricedStock2, pricedStock3));
        when(getRemoteStockServiceFactory().getForMarket(TWITTER)).thenReturn(remoteStockService);

        Response response = target("/v1/market/twitter/stocks/search").queryParam("q", "searchterm").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        List<PricedStock> results = response.readEntity(new PricedStockListGenericType());
        assertEquals(3, results.size());
        assertTrue(results.contains(pricedStock1));
        assertTrue(results.contains(pricedStock2));
        assertTrue(results.contains(pricedStock3));
    }
}
