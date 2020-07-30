package vstocks.rest.resource.v1.market;

import org.junit.Test;
import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GetAllMarketsIT extends ResourceTest {
    private final Market market1 = new Market().setId("id1").setName("name1");
    private final Market market2 = new Market().setId("id2").setName("name2");
    private final Market market3 = new Market().setId("id3").setName("name3");

    @Test
    public void testAllMarketsNone() {
        Response response = target("/v1/markets").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Market> results = response.readEntity(new MarketResultsGenericType());
        assertEquals(1, results.getPage().getPage());
        assertEquals(25, results.getPage().getSize());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testAllMarketsOnePage() {
        getDatabaseServiceFactory().getMarketService().add(market1);
        getDatabaseServiceFactory().getMarketService().add(market2);
        getDatabaseServiceFactory().getMarketService().add(market3);

        Response response = target("/v1/markets").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Market> results = response.readEntity(new MarketResultsGenericType());
        assertEquals(1, results.getPage().getPage());
        assertEquals(25, results.getPage().getSize());
        assertEquals(3, results.getTotal());
        assertTrue(results.getResults().contains(market1));
        assertTrue(results.getResults().contains(market2));
        assertTrue(results.getResults().contains(market3));
    }

    @Test
    public void testAllMarketsSpecificPage() {
        getDatabaseServiceFactory().getMarketService().add(market1);
        getDatabaseServiceFactory().getMarketService().add(market2);
        getDatabaseServiceFactory().getMarketService().add(market3);

        Response response = target("/v1/markets").queryParam("pageNum", 2).queryParam("pageSize", 1).request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Results<Market> results = response.readEntity(new MarketResultsGenericType());
        assertEquals(2, results.getPage().getPage());
        assertEquals(1, results.getPage().getSize());
        assertEquals(3, results.getTotal());
        assertTrue(results.getResults().contains(market2));
    }
}
