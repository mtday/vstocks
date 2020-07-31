package vstocks.rest.resource.v1.market.stock;

import org.junit.Before;
import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.Stock;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class GetStockIT extends ResourceTest {
    private final Stock stock = new Stock().setMarket(TWITTER).setName("name").setSymbol("symbol");

    @Before
    public void setup() {
        getDatabaseServiceFactory().getStockService().add(stock);
    }

    @Test
    public void testMarketMissing() {
        Response response = target("/v1/market/missing/stock/stockId").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());
    }

    @Test
    public void testStockMissing() {
        Response response = target("/v1/market/twitter/stock/missing").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Stock TWITTER/missing not found", error.getMessage());
    }

    @Test
    public void testMarketExists() {
        Response response = target("/v1/market/twitter/stock/symbol").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Stock fetched = response.readEntity(Stock.class);
        assertEquals(stock.getMarket(), fetched.getMarket());
        assertEquals(stock.getName(), fetched.getName());
        assertEquals(stock.getSymbol(), fetched.getSymbol());
    }
}
