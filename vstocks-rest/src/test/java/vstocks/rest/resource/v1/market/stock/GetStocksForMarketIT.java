package vstocks.rest.resource.v1.market.stock;

import org.junit.Test;
import vstocks.model.*;
import vstocks.rest.ResourceTest;
import vstocks.service.db.PricedStockService;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
        Response response = target("/v1/market/missing/stocks").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());
    }

    @Test
    public void testGetForMarketsNone() {
        PricedStockService pricedStockService = mock(PricedStockService.class);
        when(pricedStockService.getForMarket(eq(TWITTER), any(), anySet())).thenReturn(new Results<>());
        when(getDatabaseServiceFactory().getPricedStockService()).thenReturn(pricedStockService);

        Response response = target("/v1/market/twitter/stocks").request().get();

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
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock1 = new PricedStock().setMarket(TWITTER).setSymbol("symbol1").setName("name1").setTimestamp(now).setPrice(10);
        PricedStock pricedStock2 = new PricedStock().setMarket(TWITTER).setSymbol("symbol2").setName("name2").setTimestamp(now).setPrice(11);
        PricedStock pricedStock3 = new PricedStock().setMarket(TWITTER).setSymbol("symbol3").setName("name3").setTimestamp(now).setPrice(12);

        Results<PricedStock> results = new Results<PricedStock>().setPage(new Page()).setTotal(3)
                .setResults(asList(pricedStock1, pricedStock2, pricedStock3));
        PricedStockService pricedStockService = mock(PricedStockService.class);
        when(pricedStockService.getForMarket(eq(TWITTER), any(), anySet())).thenReturn(results);
        when(getDatabaseServiceFactory().getPricedStockService()).thenReturn(pricedStockService);

        Response response = target("/v1/market/twitter/stocks").request().get();

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
