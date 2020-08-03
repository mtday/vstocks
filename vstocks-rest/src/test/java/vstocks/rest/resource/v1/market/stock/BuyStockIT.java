package vstocks.rest.resource.v1.market.stock;

import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.UserStock;
import vstocks.rest.ResourceTest;
import vstocks.service.db.UserStockService;

import javax.ws.rs.core.Response;
import java.util.Optional;

import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static vstocks.model.Market.TWITTER;

public class BuyStockIT extends ResourceTest {
    @Test
    public void testMarketMissing() {
        UserStockService userStockService = mock(UserStockService.class);
        when(getDatabaseServiceFactory().getUserStockService()).thenReturn(userStockService);

        Response response = target("/v1/market/missing/stock/symbol/buy/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());

        verify(userStockService, times(0)).buyStock(any(), any(), any(), anyInt());
        verify(userStockService, times(0)).get(any(), any(), any());
    }

    @Test
    public void testStockMissing() {
        UserStockService userStockService = mock(UserStockService.class);
        when(userStockService.buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10))).thenReturn(0);
        when(getDatabaseServiceFactory().getUserStockService()).thenReturn(userStockService);

        Response response = target("/v1/market/twitter/stock/symbol/buy/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), error.getStatus());
        assertEquals("Failed to buy 10 shares of TWITTER/symbol stock", error.getMessage());

        verify(userStockService, times(1)).buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10));
        verify(userStockService, times(0)).get(any(), any(), any());
    }

    @Test
    public void testBuySuccessStockFound() {
        UserStock userStock = new UserStock().setUserId("TW:12345").setMarket(TWITTER).setSymbol("symbol").setShares(10);
        UserStockService userStockService = mock(UserStockService.class);
        when(userStockService.buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10))).thenReturn(1);
        when(userStockService.get(eq("TW:12345"), eq(TWITTER), eq("symbol"))).thenReturn(Optional.of(userStock));
        when(getDatabaseServiceFactory().getUserStockService()).thenReturn(userStockService);

        Response response = target("/v1/market/twitter/stock/symbol/buy/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        UserStock fetched = response.readEntity(UserStock.class);
        assertEquals(userStock.getUserId(), fetched.getUserId());
        assertEquals(userStock.getMarket(), fetched.getMarket());
        assertEquals(userStock.getSymbol(), fetched.getSymbol());
        assertEquals(userStock.getShares(), fetched.getShares());

        verify(userStockService, times(1)).buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10));
        verify(userStockService, times(1)).get(eq("TW:12345"), eq(TWITTER), eq("symbol"));
    }

    @Test
    public void testBuySuccessStockNotFound() {
        UserStockService userStockService = mock(UserStockService.class);
        when(userStockService.buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10))).thenReturn(1);
        when(userStockService.get(eq("TW:12345"), eq(TWITTER), eq("symbol"))).thenReturn(Optional.empty());
        when(getDatabaseServiceFactory().getUserStockService()).thenReturn(userStockService);

        Response response = target("/v1/market/twitter/stock/symbol/buy/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock TWITTER/symbol not found", error.getMessage());

        verify(userStockService, times(1)).buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10));
        verify(userStockService, times(1)).get(eq("TW:12345"), eq(TWITTER), eq("symbol"));
    }
}
