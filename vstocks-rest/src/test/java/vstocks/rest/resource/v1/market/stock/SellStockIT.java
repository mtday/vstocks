package vstocks.rest.resource.v1.market.stock;

import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.PricedUserStock;
import vstocks.rest.ResourceTest;
import vstocks.service.db.PricedUserStockService;
import vstocks.service.db.UserStockService;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.util.Optional.empty;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static vstocks.model.Market.TWITTER;

public class SellStockIT extends ResourceTest {
    @Test
    public void testMarketMissing() {
        UserStockService userStockService = mock(UserStockService.class);
        when(getDatabaseServiceFactory().getUserStockService()).thenReturn(userStockService);
        PricedUserStockService pricedUserStockService = mock(PricedUserStockService.class);
        when(getDatabaseServiceFactory().getPricedUserStockService()).thenReturn(pricedUserStockService);

        Response response = target("/v1/market/missing/stock/symbol/sell/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());

        verify(userStockService, times(0)).sellStock(any(), any(), any(), anyInt());
        verify(pricedUserStockService, times(0)).get(any(), any(), any());
    }

    @Test
    public void testStockMissing() {
        UserStockService userStockService = mock(UserStockService.class);
        when(userStockService.sellStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10))).thenReturn(0);
        when(getDatabaseServiceFactory().getUserStockService()).thenReturn(userStockService);
        PricedUserStockService pricedUserStockService = mock(PricedUserStockService.class);
        when(getDatabaseServiceFactory().getPricedUserStockService()).thenReturn(pricedUserStockService);

        Response response = target("/v1/market/twitter/stock/symbol/sell/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), error.getStatus());
        assertEquals("Failed to sell 10 shares of TWITTER/symbol stock", error.getMessage());

        verify(userStockService, times(1)).sellStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10));
        verify(pricedUserStockService, times(0)).get(any(), any(), any());
    }

    @Test
    public void testSellSuccessStockFound() {
        UserStockService userStockService = mock(UserStockService.class);
        when(userStockService.sellStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10))).thenReturn(1);
        when(getDatabaseServiceFactory().getUserStockService()).thenReturn(userStockService);
        PricedUserStockService pricedUserStockService = mock(PricedUserStockService.class);
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock().setUserId("TW:12345").setMarket(TWITTER).setSymbol("symbol").setShares(10).setTimestamp(now).setPrice(10);
        when(pricedUserStockService.get(eq("TW:12345"), eq(TWITTER), eq("symbol"))).thenReturn(Optional.of(pricedUserStock));
        when(getDatabaseServiceFactory().getPricedUserStockService()).thenReturn(pricedUserStockService);

        Response response = target("/v1/market/twitter/stock/symbol/sell/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        PricedUserStock fetched = response.readEntity(PricedUserStock.class);
        assertEquals(pricedUserStock.getUserId(), fetched.getUserId());
        assertEquals(pricedUserStock.getMarket(), fetched.getMarket());
        assertEquals(pricedUserStock.getSymbol(), fetched.getSymbol());
        assertEquals(pricedUserStock.getShares(), fetched.getShares());
        assertEquals(pricedUserStock.getTimestamp(), fetched.getTimestamp());
        assertEquals(pricedUserStock.getPrice(), fetched.getPrice());

        verify(userStockService, times(1)).sellStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10));
        verify(pricedUserStockService, times(1)).get(eq("TW:12345"), eq(TWITTER), eq("symbol"));
    }

    @Test
    public void testSellSuccessStockNotFound() {
        UserStockService userStockService = mock(UserStockService.class);
        when(userStockService.sellStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10))).thenReturn(1);
        when(getDatabaseServiceFactory().getUserStockService()).thenReturn(userStockService);
        PricedUserStockService pricedUserStockService = mock(PricedUserStockService.class);
        when(pricedUserStockService.get(eq("TW:12345"), eq(TWITTER), eq("symbol"))).thenReturn(empty());
        when(getDatabaseServiceFactory().getPricedUserStockService()).thenReturn(pricedUserStockService);

        Response response = target("/v1/market/twitter/stock/symbol/sell/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        PricedUserStock fetched = response.readEntity(PricedUserStock.class);
        assertEquals("TW:12345", fetched.getUserId());
        assertEquals(TWITTER, fetched.getMarket());
        assertEquals("symbol", fetched.getSymbol());
        assertEquals(0, fetched.getShares());
        assertNotNull(fetched.getTimestamp());
        assertEquals(1, fetched.getPrice());

        verify(userStockService, times(1)).sellStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10));
        verify(pricedUserStockService, times(1)).get(eq("TW:12345"), eq(TWITTER), eq("symbol"));
    }
}
