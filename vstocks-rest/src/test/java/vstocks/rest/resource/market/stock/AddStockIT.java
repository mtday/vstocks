package vstocks.rest.resource.market.stock;

import org.junit.Test;
import vstocks.db.PricedStockDB;
import vstocks.model.ErrorResponse;
import vstocks.model.PricedStock;
import vstocks.rest.ResourceTest;
import vstocks.service.remote.RemoteStockService;

import javax.ws.rs.core.Response;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static vstocks.model.Market.TWITTER;

public class AddStockIT extends ResourceTest {
    @Test
    public void testMarketMissing() {
        PricedStockDB pricedStockDb = mock(PricedStockDB.class);
        when(getDBFactory().getPricedStockDB()).thenReturn(pricedStockDb);

        Response response = target("/market/missing/stock/symbol").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());

        verify(pricedStockDb, times(0)).add(any());
    }

    @Test
    public void testStockMissing() {
        PricedStockDB pricedStockDb = mock(PricedStockDB.class);
        when(getDBFactory().getPricedStockDB()).thenReturn(pricedStockDb);

        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("symbol"), eq(10))).thenReturn(emptyList());
        when(getRemoteStockServiceFactory().getForMarket(eq(TWITTER))).thenReturn(remoteStockService);

        Response response = target("/market/twitter/stock/symbol").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock Twitter/symbol not found", error.getMessage());

        verify(pricedStockDb, times(0)).add(any());
    }

    @Test
    public void testStockFoundExactMatch() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("name").setTimestamp(now).setPrice(10);
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("symbol"), eq(10))).thenReturn(singletonList(pricedStock));
        when(getRemoteStockServiceFactory().getForMarket(eq(TWITTER))).thenReturn(remoteStockService);

        PricedStockDB pricedStockDb = mock(PricedStockDB.class);
        when(getDBFactory().getPricedStockDB()).thenReturn(pricedStockDb);

        Response response = target("/market/twitter/stock/symbol").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        PricedStock fetched = response.readEntity(PricedStock.class);
        assertEquals(pricedStock.getMarket(), fetched.getMarket());
        assertEquals(pricedStock.getSymbol(), fetched.getSymbol());
        assertEquals(pricedStock.getName(), fetched.getName());
        assertEquals(pricedStock.getTimestamp(), fetched.getTimestamp());
        assertEquals(pricedStock.getPrice(), fetched.getPrice());

        verify(pricedStockDb, times(1)).add(eq(pricedStock));
    }

    @Test
    public void testStockFoundWrongCase() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("name").setTimestamp(now).setPrice(10);
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("SYMBOL"), eq(10))).thenReturn(singletonList(pricedStock));
        when(getRemoteStockServiceFactory().getForMarket(eq(TWITTER))).thenReturn(remoteStockService);

        PricedStockDB pricedStockDb = mock(PricedStockDB.class);
        when(getDBFactory().getPricedStockDB()).thenReturn(pricedStockDb);

        Response response = target("/market/twitter/stock/SYMBOL").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        PricedStock fetched = response.readEntity(PricedStock.class);
        assertEquals(pricedStock.getMarket(), fetched.getMarket());
        assertEquals(pricedStock.getSymbol(), fetched.getSymbol()); // the actual symbol from the remote service is used
        assertEquals(pricedStock.getName(), fetched.getName());
        assertEquals(pricedStock.getTimestamp(), fetched.getTimestamp());
        assertEquals(pricedStock.getPrice(), fetched.getPrice());

        verify(pricedStockDb, times(1)).add(eq(pricedStock));
    }

    @Test
    public void testStockFoundPrefixMatch() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("name").setTimestamp(now).setPrice(10);
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("sym"), eq(10))).thenReturn(singletonList(pricedStock));
        when(getRemoteStockServiceFactory().getForMarket(eq(TWITTER))).thenReturn(remoteStockService);

        PricedStockDB pricedStockDb = mock(PricedStockDB.class);
        when(getDBFactory().getPricedStockDB()).thenReturn(pricedStockDb);

        Response response = target("/market/twitter/stock/sym").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock Twitter/sym not found", error.getMessage());

        verify(pricedStockDb, times(0)).add(any());
    }

    @Test
    public void testMultipleStocksFoundIncludingValidMatch() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedStock pricedStock1 = new PricedStock().setMarket(TWITTER).setSymbol("symbol1").setName("name1").setTimestamp(now).setPrice(10);
        PricedStock pricedStock2 = new PricedStock().setMarket(TWITTER).setSymbol("symbol2").setName("name2").setTimestamp(now).setPrice(10);
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("symbol"), eq(10))).thenReturn(asList(pricedStock1, pricedStock2));
        when(getRemoteStockServiceFactory().getForMarket(eq(TWITTER))).thenReturn(remoteStockService);

        PricedStockDB pricedStockDb = mock(PricedStockDB.class);
        when(getDBFactory().getPricedStockDB()).thenReturn(pricedStockDb);

        Response response = target("/market/twitter/stock/symbol").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock Twitter/symbol not found", error.getMessage());

        verify(pricedStockDb, times(0)).add(any());
    }
}
