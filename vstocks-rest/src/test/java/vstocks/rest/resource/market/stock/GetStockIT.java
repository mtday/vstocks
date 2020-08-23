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

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());
    }

    @Test
    public void testStockMissing() {
        PricedStockService pricedStockDb = mock(PricedStockService.class);
        when(pricedStockDb.get(eq(TWITTER), eq("missing"))).thenReturn(empty());
        when(getDBFactory().getPricedStockService()).thenReturn(pricedStockDb);

        Response response = target("/market/twitter/stock/missing").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock Twitter/missing not found", error.getMessage());
    }

    @Test
    public void testMarketExists() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setName("name").setSymbol("symbol").setTimestamp(now).setPrice(10);
        PricedStockService pricedStockDb = mock(PricedStockService.class);
        when(pricedStockDb.get(eq(TWITTER), eq("symbol"))).thenReturn(Optional.of(pricedStock));
        when(getDBFactory().getPricedStockService()).thenReturn(pricedStockDb);

        Response response = target("/market/twitter/stock/symbol").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        PricedStock fetched = response.readEntity(PricedStock.class);
        assertEquals(pricedStock.getMarket(), fetched.getMarket());
        assertEquals(pricedStock.getName(), fetched.getName());
        assertEquals(pricedStock.getSymbol(), fetched.getSymbol());
        assertEquals(pricedStock.getTimestamp(), fetched.getTimestamp());
        assertEquals(pricedStock.getPrice(), fetched.getPrice());
    }
}
