package vstocks.rest.resource.market.stock;

import org.junit.Test;
import vstocks.db.PricedStockService;
import vstocks.model.ErrorResponse;
import vstocks.model.PricedStock;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class GetStockIT extends ResourceTest {
    @Test
    public void testMarketMissing() {
        Response response = target("/market/missing/stock/symbol").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":404,\"message\":\"Market missing not found\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), errorResponse.getStatus());
        assertEquals("Market missing not found", errorResponse.getMessage());
    }

    @Test
    public void testStockMissing() {
        PricedStockService pricedStockService = mock(PricedStockService.class);
        when(pricedStockService.get(eq(TWITTER), eq("missing"))).thenReturn(empty());
        when(getServiceFactory().getPricedStockService()).thenReturn(pricedStockService);

        Response response = target("/market/twitter/stock/missing").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":404,\"message\":\"Stock Twitter/missing not found\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), errorResponse.getStatus());
        assertEquals("Stock Twitter/missing not found", errorResponse.getMessage());
    }

    @Test
    public void testMarketExists() {
        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setName("name")
                .setSymbol("symbol")
                .setTimestamp(timestamp)
                .setPrice(10);

        PricedStockService pricedStockService = mock(PricedStockService.class);
        when(pricedStockService.get(eq(TWITTER), eq("symbol"))).thenReturn(Optional.of(pricedStock));
        when(getServiceFactory().getPricedStockService()).thenReturn(pricedStockService);

        Response response = target("/market/twitter/stock/symbol").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"market\":\"Twitter\",\"symbol\":\"symbol\",\"timestamp\":\"2020-12-03T10:15:30Z\","
                + "\"name\":\"name\",\"profileImage\":null,\"price\":10}", json);

        PricedStock fetched = convert(json, PricedStock.class);
        assertEquals(pricedStock.getMarket(), fetched.getMarket());
        assertEquals(pricedStock.getName(), fetched.getName());
        assertEquals(pricedStock.getSymbol(), fetched.getSymbol());
        assertEquals(pricedStock.getTimestamp(), fetched.getTimestamp());
        assertEquals(pricedStock.getPrice(), fetched.getPrice());
    }
}
