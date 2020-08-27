package vstocks.rest.resource.market.stock;

import org.junit.Test;
import vstocks.db.PricedUserStockService;
import vstocks.db.UserStockService;
import vstocks.model.ErrorResponse;
import vstocks.model.PricedUserStock;
import vstocks.model.User;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Optional.empty;
import static javax.ws.rs.client.Entity.entity;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static vstocks.model.Market.TWITTER;

public class SellStockIT extends ResourceTest {
    @Test
    public void testMarketMissing() {
        UserStockService userStockService = mock(UserStockService.class);
        when(getServiceFactory().getUserStockService()).thenReturn(userStockService);
        PricedUserStockService pricedUserStockService = mock(PricedUserStockService.class);
        when(getServiceFactory().getPricedUserStockService()).thenReturn(pricedUserStockService);

        Response response = target("/market/missing/stock/symbol/sell/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":404,\"message\":\"Market missing not found\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), errorResponse.getStatus());
        assertEquals("Market missing not found", errorResponse.getMessage());

        verify(userStockService, times(0)).sellStock(any(), any(), any(), anyLong());
        verify(pricedUserStockService, times(0)).get(any(), any(), any());
    }

    @Test
    public void testStockMissing() {
        User user = getUser();
        UserStockService userStockService = mock(UserStockService.class);
        when(userStockService.sellStock(eq(user.getId()), eq(TWITTER), eq("symbol"), eq(10L))).thenReturn(0);
        when(getServiceFactory().getUserStockService()).thenReturn(userStockService);
        PricedUserStockService pricedUserStockService = mock(PricedUserStockService.class);
        when(getServiceFactory().getPricedUserStockService()).thenReturn(pricedUserStockService);

        Response response = target("/market/twitter/stock/symbol/sell/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":400,\"message\":\"Failed to sell 10 shares of Twitter/symbol stock\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), errorResponse.getStatus());
        assertEquals("Failed to sell 10 shares of Twitter/symbol stock", errorResponse.getMessage());

        verify(userStockService, times(1)).sellStock(eq(user.getId()), eq(TWITTER), eq("symbol"), eq(10L));
        verify(pricedUserStockService, times(0)).get(any(), any(), any());
    }

    @Test
    public void testSellSuccessStockFound() {
        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(getUser().getId())
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("image")
                .setTimestamp(timestamp)
                .setShares(10)
                .setPrice(10)
                .setValue(10 * 10);

        User user = getUser();
        UserStockService userStockService = mock(UserStockService.class);
        when(userStockService.sellStock(eq(user.getId()), eq(TWITTER), eq("symbol"), eq(10L))).thenReturn(1);
        when(getServiceFactory().getUserStockService()).thenReturn(userStockService);
        PricedUserStockService pricedUserStockService = mock(PricedUserStockService.class);
        when(pricedUserStockService.get(eq(user.getId()), eq(TWITTER), eq("symbol"))).thenReturn(Optional.of(pricedUserStock));
        when(getServiceFactory().getPricedUserStockService()).thenReturn(pricedUserStockService);

        Response response = target("/market/twitter/stock/symbol/sell/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"market\":\"Twitter\","
                + "\"symbol\":\"symbol\",\"name\":\"name\",\"profileImage\":\"image\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"shares\":10,\"price\":10,\"value\":100}", json);

        PricedUserStock fetched = convert(json, PricedUserStock.class);
        assertEquals(pricedUserStock.getUserId(), fetched.getUserId());
        assertEquals(pricedUserStock.getMarket(), fetched.getMarket());
        assertEquals(pricedUserStock.getSymbol(), fetched.getSymbol());
        assertEquals(pricedUserStock.getShares(), fetched.getShares());
        assertEquals(pricedUserStock.getTimestamp(), fetched.getTimestamp());
        assertEquals(pricedUserStock.getPrice(), fetched.getPrice());

        verify(userStockService, times(1)).sellStock(eq(user.getId()), eq(TWITTER), eq("symbol"), eq(10L));
        verify(pricedUserStockService, times(1)).get(eq(user.getId()), eq(TWITTER), eq("symbol"));
    }

    @Test
    public void testSellSuccessStockNotFound() {
        User user = getUser();
        UserStockService userStockService = mock(UserStockService.class);
        when(userStockService.sellStock(eq(user.getId()), eq(TWITTER), eq("symbol"), eq(10L))).thenReturn(1);
        when(getServiceFactory().getUserStockService()).thenReturn(userStockService);
        PricedUserStockService pricedUserStockService = mock(PricedUserStockService.class);
        when(pricedUserStockService.get(eq(user.getId()), eq(TWITTER), eq("symbol"))).thenReturn(empty());
        when(getServiceFactory().getPricedUserStockService()).thenReturn(pricedUserStockService);

        Response response = target("/market/twitter/stock/symbol/sell/10").request().post(entity("", APPLICATION_JSON_TYPE));

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":404,\"message\":\"Stock Twitter/symbol not found\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), errorResponse.getStatus());
        assertEquals("Stock Twitter/symbol not found", errorResponse.getMessage());

        verify(userStockService, times(1)).sellStock(any(), any(), any(), anyLong());
        verify(pricedUserStockService, times(1)).get(any(), any(), any());
    }
}
