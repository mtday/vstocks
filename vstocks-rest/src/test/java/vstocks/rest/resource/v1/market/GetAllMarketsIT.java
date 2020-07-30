package vstocks.rest.resource.v1.market;

import org.junit.Test;
import vstocks.model.Market;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;

public class GetAllMarketsIT extends ResourceTest {
    @Test
    public void testGetAllMarkets() {
        Response response = target("/v1/markets").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        List<Market> results = response.readEntity(new MarketListGenericType());
        assertEquals(2, results.size());
        assertTrue(results.contains(TWITTER));
        assertTrue(results.contains(YOUTUBE));
    }
}
