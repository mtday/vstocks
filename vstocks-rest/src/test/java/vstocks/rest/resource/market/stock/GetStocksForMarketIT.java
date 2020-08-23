package vstocks.rest.resource.market.stock;

import org.junit.Test;
import vstocks.db.PricedStockService;
import vstocks.model.ErrorResponse;
import vstocks.model.Page;
import vstocks.model.PricedStock;
import vstocks.model.Results;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class GetStocksForMarketIT extends ResourceTest {
    @Test
    public void testGetForMarketMissing() {
        Response response = target("/market/missing/stocks").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());
    }

    @Test
    public void testGetForMarketsNone() {
        PricedStockService pricedStockDb = mock(PricedStockService.class);
        when(pricedStockDb.getForMarket(eq(TWITTER), any(), any())).thenReturn(new Results<>());
        when(getServiceFactory().getPricedStockService()).thenReturn(pricedStockDb);

        Response response = target("/market/twitter/stocks").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<PricedStock> results = response.readEntity(new PricedStockResultsGenericType());
        assertEquals(1, results.getPage().getPage());
        assertEquals(25, results.getPage().getSize());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForMarketsOnePage() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PricedStock pricedStock1 = new PricedStock().setMarket(TWITTER).setSymbol("symbol1").setName("name1").setTimestamp(now).setPrice(10);
        PricedStock pricedStock2 = new PricedStock().setMarket(TWITTER).setSymbol("symbol2").setName("name2").setTimestamp(now).setPrice(11);
        PricedStock pricedStock3 = new PricedStock().setMarket(TWITTER).setSymbol("symbol3").setName("name3").setTimestamp(now).setPrice(12);

        Results<PricedStock> results = new Results<PricedStock>().setPage(new Page()).setTotal(3)
                .setResults(asList(pricedStock1, pricedStock2, pricedStock3));
        PricedStockService pricedStockDb = mock(PricedStockService.class);
        when(pricedStockDb.getForMarket(eq(TWITTER), any(), any())).thenReturn(results);
        when(getServiceFactory().getPricedStockService()).thenReturn(pricedStockDb);

        Response response = target("/market/twitter/stocks").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<PricedStock> fetched = response.readEntity(new PricedStockResultsGenericType());
        assertEquals(1, fetched.getPage().getPage());
        assertEquals(25, fetched.getPage().getSize());
        assertEquals(3, fetched.getTotal());
        assertTrue(fetched.getResults().contains(pricedStock1));
        assertTrue(fetched.getResults().contains(pricedStock2));
        assertTrue(fetched.getResults().contains(pricedStock3));
    }
}
