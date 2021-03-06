package vstocks.rest.resource.market.stock;

import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.PricedStock;
import vstocks.rest.ResourceTest;
import vstocks.service.remote.RemoteStockService;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class SearchStocksIT extends ResourceTest {
    @Test
    public void testSearchStocksMarketMissing() {
        Response response = target("/market/missing/stocks/search").queryParam("q", "searchterm").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":404,\"message\":\"Market missing not found\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), errorResponse.getStatus());
        assertEquals("Market missing not found", errorResponse.getMessage());
    }

    @Test
    public void testSearchStocksNoSearchParam() {
        Response response = target("/market/twitter/stocks/search").request().get();

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":400,\"message\":\"Missing required 'q' query parameter\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing required 'q' query parameter", errorResponse.getMessage());
    }

    @Test
    public void testSearchStocksNone() {
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("searchterm"), eq(20))).thenReturn(emptyList());
        when(getRemoteStockServiceFactory().getForMarket(TWITTER)).thenReturn(remoteStockService);

        Response response = target("/market/twitter/stocks/search").queryParam("q", "searchterm").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("[]", json);

        List<PricedStock> fetched = convert(json, new PricedStockListTypeRef());
        assertTrue(fetched.isEmpty());
    }

    @Test
    public void testSearchStocksSome() {
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

        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.search(eq("searchterm"), eq(20))).thenReturn(asList(pricedStock1, pricedStock2, pricedStock3));
        when(getRemoteStockServiceFactory().getForMarket(TWITTER)).thenReturn(remoteStockService);

        Response response = target("/market/twitter/stocks/search").queryParam("q", "searchterm").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String price1json = "{\"market\":\"Twitter\",\"symbol\":\"symbol1\",\"timestamp\":\"2020-12-03T10:15:30Z\","
                + "\"name\":\"name1\",\"profileImage\":\"link1\",\"price\":10}";
        String price2json = "{\"market\":\"Twitter\",\"symbol\":\"symbol2\",\"timestamp\":\"2020-12-03T10:15:30Z\","
                + "\"name\":\"name2\",\"profileImage\":\"link2\",\"price\":11}";
        String price3json = "{\"market\":\"Twitter\",\"symbol\":\"symbol3\",\"timestamp\":\"2020-12-03T10:15:30Z\","
                + "\"name\":\"name3\",\"profileImage\":\"link3\",\"price\":12}";
        assertEquals("[" + price1json + "," + price2json + "," + price3json + "]", json);

        List<PricedStock> results = convert(json, new PricedStockListTypeRef());
        assertEquals(3, results.size());
        assertTrue(results.contains(pricedStock1));
        assertTrue(results.contains(pricedStock2));
        assertTrue(results.contains(pricedStock3));
    }
}
