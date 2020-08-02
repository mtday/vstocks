package vstocks.rest.resource.v1.market.stock;

import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.PricedStock;
import vstocks.rest.ResourceTest;
import vstocks.service.db.StockPriceService;
import vstocks.service.db.StockService;
import vstocks.service.remote.RemoteStockService;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
        StockService stockService = mock(StockService.class);
        when(getDatabaseServiceFactory().getStockService()).thenReturn(stockService);
        StockPriceService stockPriceService = mock(StockPriceService.class);
        when(getDatabaseServiceFactory().getStockPriceService()).thenReturn(stockPriceService);

        Response response = target("/v1/market/missing/stock/symbol").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());

        verify(stockService, times(0)).add(any());
        verify(stockPriceService, times(0)).add(any());
    }

    @Test
    public void testStockMissing() {
        StockService stockService = mock(StockService.class);
        when(getDatabaseServiceFactory().getStockService()).thenReturn(stockService);
        StockPriceService stockPriceService = mock(StockPriceService.class);
        when(getDatabaseServiceFactory().getStockPriceService()).thenReturn(stockPriceService);

        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("symbol"), eq(10))).thenReturn(emptyList());
        when(getRemoteStockServiceFactory().getForMarket(eq(TWITTER))).thenReturn(remoteStockService);

        Response response = target("/v1/market/twitter/stock/symbol").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock TWITTER/symbol not found", error.getMessage());

        verify(stockService, times(0)).add(any());
        verify(stockPriceService, times(0)).add(any());
    }

    @Test
    public void testStockFoundExactMatch() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("name").setTimestamp(now).setPrice(10);
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("symbol"), eq(10))).thenReturn(singletonList(pricedStock));
        when(getRemoteStockServiceFactory().getForMarket(eq(TWITTER))).thenReturn(remoteStockService);

        StockService stockService = mock(StockService.class);
        when(getDatabaseServiceFactory().getStockService()).thenReturn(stockService);
        StockPriceService stockPriceService = mock(StockPriceService.class);
        when(getDatabaseServiceFactory().getStockPriceService()).thenReturn(stockPriceService);

        Response response = target("/v1/market/twitter/stock/symbol").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        PricedStock fetched = response.readEntity(PricedStock.class);
        assertEquals(pricedStock.getMarket(), fetched.getMarket());
        assertEquals(pricedStock.getSymbol(), fetched.getSymbol());
        assertEquals(pricedStock.getName(), fetched.getName());
        assertEquals(pricedStock.getTimestamp(), fetched.getTimestamp());
        assertEquals(pricedStock.getPrice(), fetched.getPrice());

        verify(stockService, times(1)).add(eq(pricedStock.asStock()));
        verify(stockPriceService, times(1)).add(eq(pricedStock.asStockPrice()));
    }

    @Test
    public void testStockFoundWrongCase() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("name").setTimestamp(now).setPrice(10);
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("SYMBOL"), eq(10))).thenReturn(singletonList(pricedStock));
        when(getRemoteStockServiceFactory().getForMarket(eq(TWITTER))).thenReturn(remoteStockService);

        StockService stockService = mock(StockService.class);
        when(getDatabaseServiceFactory().getStockService()).thenReturn(stockService);
        StockPriceService stockPriceService = mock(StockPriceService.class);
        when(getDatabaseServiceFactory().getStockPriceService()).thenReturn(stockPriceService);

        Response response = target("/v1/market/twitter/stock/SYMBOL").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        PricedStock fetched = response.readEntity(PricedStock.class);
        assertEquals(pricedStock.getMarket(), fetched.getMarket());
        assertEquals(pricedStock.getSymbol(), fetched.getSymbol()); // the actual symbol from the remote service is used
        assertEquals(pricedStock.getName(), fetched.getName());
        assertEquals(pricedStock.getTimestamp(), fetched.getTimestamp());
        assertEquals(pricedStock.getPrice(), fetched.getPrice());

        verify(stockService, times(1)).add(eq(pricedStock.asStock()));
        verify(stockPriceService, times(1)).add(eq(pricedStock.asStockPrice()));
    }

    @Test
    public void testStockFoundPrefixMatch() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("name").setTimestamp(now).setPrice(10);
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("sym"), eq(10))).thenReturn(singletonList(pricedStock));
        when(getRemoteStockServiceFactory().getForMarket(eq(TWITTER))).thenReturn(remoteStockService);

        StockService stockService = mock(StockService.class);
        when(getDatabaseServiceFactory().getStockService()).thenReturn(stockService);
        StockPriceService stockPriceService = mock(StockPriceService.class);
        when(getDatabaseServiceFactory().getStockPriceService()).thenReturn(stockPriceService);

        Response response = target("/v1/market/twitter/stock/sym").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock TWITTER/sym not found", error.getMessage());

        verify(stockService, times(0)).add(any());
        verify(stockPriceService, times(0)).add(any());
    }

    @Test
    public void testMultipleStocksFoundIncludingValidMatch() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock1 = new PricedStock().setMarket(TWITTER).setSymbol("symbol1").setName("name1").setTimestamp(now).setPrice(10);
        PricedStock pricedStock2 = new PricedStock().setMarket(TWITTER).setSymbol("symbol2").setName("name2").setTimestamp(now).setPrice(10);
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("symbol"), eq(10))).thenReturn(asList(pricedStock1, pricedStock2));
        when(getRemoteStockServiceFactory().getForMarket(eq(TWITTER))).thenReturn(remoteStockService);

        StockService stockService = mock(StockService.class);
        when(getDatabaseServiceFactory().getStockService()).thenReturn(stockService);
        StockPriceService stockPriceService = mock(StockPriceService.class);
        when(getDatabaseServiceFactory().getStockPriceService()).thenReturn(stockPriceService);

        Response response = target("/v1/market/twitter/stock/symbol").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock TWITTER/symbol not found", error.getMessage());

        verify(stockService, times(0)).add(any());
        verify(stockPriceService, times(0)).add(any());
    }
}
