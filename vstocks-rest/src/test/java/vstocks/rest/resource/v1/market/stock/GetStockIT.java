package vstocks.rest.resource.v1.market.stock;

import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.PricedStock;
import vstocks.rest.ResourceTest;
import vstocks.db.PricedStockDB;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class GetStockIT extends ResourceTest {
    @Test
    public void testMarketMissing() {
        Response response = target("/v1/market/missing/stock/symbol").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());
    }

    @Test
    public void testStockMissing() {
        PricedStockDB pricedStockDb = mock(PricedStockDB.class);
        when(pricedStockDb.get(eq(TWITTER), eq("missing"))).thenReturn(empty());
        when(getDBFactory().getPricedStockDB()).thenReturn(pricedStockDb);

        Response response = target("/v1/market/twitter/stock/missing").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock TWITTER/missing not found", error.getMessage());
    }

    @Test
    public void testMarketExists() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setName("name").setSymbol("symbol").setTimestamp(now).setPrice(10);
        PricedStockDB pricedStockDb = mock(PricedStockDB.class);
        when(pricedStockDb.get(eq(TWITTER), eq("symbol"))).thenReturn(Optional.of(pricedStock));
        when(getDBFactory().getPricedStockDB()).thenReturn(pricedStockDb);

        Response response = target("/v1/market/twitter/stock/symbol").request().get();

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
