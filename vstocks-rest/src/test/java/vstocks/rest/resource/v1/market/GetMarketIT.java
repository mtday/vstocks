package vstocks.rest.resource.v1.market;

import org.junit.Test;
import vstocks.model.ErrorResponse;
import vstocks.model.Market;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class GetMarketIT extends ResourceTest {
    @Test
    public void testMarketMissing() {
        Response response = target("/v1/market/missing").request().get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse error = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), error.getStatus());
        assertEquals("Market missing not found", error.getMessage());
    }

    @Test
    public void testMarketExistsExactMatch() {
        Response response = target("/v1/market/TWITTER").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Market fetched = response.readEntity(Market.class);
        assertEquals(TWITTER, fetched);
    }

    @Test
    public void testMarketExistsWrongCase() {
        Response response = target("/v1/market/twitter").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        Market fetched = response.readEntity(Market.class);
        assertEquals(TWITTER, fetched);
    }
}
