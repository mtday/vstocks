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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class GetStocksForMarketIT extends ResourceTest {
    @Test
    public void testGetForMarketMissing() {
        Response response = target("/market/missing/stocks").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":404,\"message\":\"Market missing not found\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), errorResponse.getStatus());
        assertEquals("Market missing not found", errorResponse.getMessage());
    }

    @Test
    public void testGetForMarketsNone() {
        Results<PricedStock> results = new Results<PricedStock>().setPage(Page.from(1, 20, 0, 0));
        PricedStockService pricedStockService = mock(PricedStockService.class);
        when(pricedStockService.getForMarket(eq(TWITTER), any(), any())).thenReturn(results);
        when(getServiceFactory().getPricedStockService()).thenReturn(pricedStockService);

        Response response = target("/market/twitter/stocks").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"page\":{\"page\":1,\"size\":20,\"totalPages\":0,\"firstRow\":null,\"lastRow\":null,"
                + "\"totalRows\":0},\"results\":[]}", json);

        Results<PricedStock> fetched = convert(json, new PricedStockResultsTypeRef());
        assertEquals(results, fetched);
    }

    @Test
    public void testGetForMarketsOnePage() {
        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        PricedStock pricedStock1 = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol1")
                .setName("name1")
                .setProfileImage("link1")
                .setTimestamp(timestamp)
                .setPrice(10);
        PricedStock pricedStock2 = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol2")
                .setName("name2")
                .setProfileImage("link2")
                .setTimestamp(timestamp)
                .setPrice(11);
        PricedStock pricedStock3 = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol3")
                .setName("name3")
                .setProfileImage("link3")
                .setTimestamp(timestamp)
                .setPrice(12);

        Results<PricedStock> results = new Results<PricedStock>().setPage(Page.from(1, 20, 3, 3))
                .setResults(asList(pricedStock1, pricedStock2, pricedStock3));
        PricedStockService pricedStockService = mock(PricedStockService.class);
        when(pricedStockService.getForMarket(eq(TWITTER), any(), any())).thenReturn(results);
        when(getServiceFactory().getPricedStockService()).thenReturn(pricedStockService);

        Response response = target("/market/twitter/stocks").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String price1json = "{\"market\":\"Twitter\",\"symbol\":\"symbol1\",\"timestamp\":\"2020-12-03T10:15:30Z\","
                + "\"name\":\"name1\",\"profileImage\":\"link1\",\"price\":10}";
        String price2json = "{\"market\":\"Twitter\",\"symbol\":\"symbol2\",\"timestamp\":\"2020-12-03T10:15:30Z\","
                + "\"name\":\"name2\",\"profileImage\":\"link2\",\"price\":11}";
        String price3json = "{\"market\":\"Twitter\",\"symbol\":\"symbol3\",\"timestamp\":\"2020-12-03T10:15:30Z\","
                + "\"name\":\"name3\",\"profileImage\":\"link3\",\"price\":12}";
        String expected = "{\"page\":{\"page\":1,\"size\":20,\"totalPages\":1,\"firstRow\":1,\"lastRow\":3,"
                + "\"totalRows\":3},\"results\":[" + price1json + "," + price2json + "," + price3json + "]}";
        assertEquals(expected, json);

        Results<PricedStock> fetched = convert(json, new PricedStockResultsTypeRef());
        assertEquals(Page.from(1, 20, 3, 3), fetched.getPage());
        assertTrue(fetched.getResults().contains(pricedStock1));
        assertTrue(fetched.getResults().contains(pricedStock2));
        assertTrue(fetched.getResults().contains(pricedStock3));
    }
}
