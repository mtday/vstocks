package vstocks.rest.resource.v1.market.stock;

import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.PricedUserStock;
import vstocks.rest.ResourceTest;
import vstocks.db.PricedUserStockDB;
import vstocks.db.UserStockDB;

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
import static org.mockito.Mockito.*;
import static vstocks.model.Market.TWITTER;

public class BuyStockIT extends ResourceTest {
    @Test
    public void testMarketMissing() {
        UserStockDB userStockDb = mock(UserStockDB.class);
        when(getDBFactory().getUserStockDB()).thenReturn(userStockDb);
        PricedUserStockDB pricedUserStockDb = mock(PricedUserStockDB.class);
        when(getDBFactory().getPricedUserStockDB()).thenReturn(pricedUserStockDb);

        Response response = target("/v1/market/missing/stock/symbol/buy/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());

        verify(userStockDb, times(0)).buyStock(any(), any(), any(), anyInt());
        verify(pricedUserStockDb, times(0)).get(any(), any(), any());
    }

    @Test
    public void testStockMissing() {
        UserStockDB userStockDb = mock(UserStockDB.class);
        when(userStockDb.buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10))).thenReturn(0);
        when(getDBFactory().getUserStockDB()).thenReturn(userStockDb);
        PricedUserStockDB pricedUserStockDb = mock(PricedUserStockDB.class);
        when(getDBFactory().getPricedUserStockDB()).thenReturn(pricedUserStockDb);

        Response response = target("/v1/market/twitter/stock/symbol/buy/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), error.getStatus());
        assertEquals("Failed to buy 10 shares of TWITTER/symbol stock", error.getMessage());

        verify(userStockDb, times(1)).buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10));
        verify(pricedUserStockDb, times(0)).get(any(), any(), any());
    }

    @Test
    public void testBuySuccessStockFound() {
        UserStockDB userStockDb = mock(UserStockDB.class);
        when(userStockDb.buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10))).thenReturn(1);
        when(getDBFactory().getUserStockDB()).thenReturn(userStockDb);
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock().setUserId("TW:12345").setMarket(TWITTER).setSymbol("symbol").setShares(10).setTimestamp(now).setPrice(10);
        PricedUserStockDB pricedUserStockDb = mock(PricedUserStockDB.class);
        when(pricedUserStockDb.get(eq("TW:12345"), eq(TWITTER), eq("symbol"))).thenReturn(Optional.of(pricedUserStock));
        when(getDBFactory().getPricedUserStockDB()).thenReturn(pricedUserStockDb);

        Response response = target("/v1/market/twitter/stock/symbol/buy/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        PricedUserStock fetched = response.readEntity(PricedUserStock.class);
        assertEquals(pricedUserStock.getUserId(), fetched.getUserId());
        assertEquals(pricedUserStock.getMarket(), fetched.getMarket());
        assertEquals(pricedUserStock.getSymbol(), fetched.getSymbol());
        assertEquals(pricedUserStock.getShares(), fetched.getShares());
        assertEquals(pricedUserStock.getTimestamp(), fetched.getTimestamp());
        assertEquals(pricedUserStock.getPrice(), fetched.getPrice());

        verify(userStockDb, times(1)).buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10));
        verify(pricedUserStockDb, times(1)).get(eq("TW:12345"), eq(TWITTER), eq("symbol"));
    }

    @Test
    public void testBuySuccessStockNotFound() {
        UserStockDB userStockDb = mock(UserStockDB.class);
        when(userStockDb.buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10))).thenReturn(1);
        when(getDBFactory().getUserStockDB()).thenReturn(userStockDb);
        PricedUserStockDB pricedUserStockDb = mock(PricedUserStockDB.class);
        when(pricedUserStockDb.get(eq("TW:12345"), eq(TWITTER), eq("symbol"))).thenReturn(empty());
        when(getDBFactory().getPricedUserStockDB()).thenReturn(pricedUserStockDb);

        Response response = target("/v1/market/twitter/stock/symbol/buy/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock TWITTER/symbol not found", error.getMessage());

        verify(userStockDb, times(1)).buyStock(eq("TW:12345"), eq(TWITTER), eq("symbol"), eq(10));
        verify(pricedUserStockDb, times(1)).get(eq("TW:12345"), eq(TWITTER), eq("symbol"));
    }
}
