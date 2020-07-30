package vstocks.rest.resource.v1.user;

import org.junit.Test;
import vstocks.model.User;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static vstocks.model.UserSource.TwitterClient;

public class GetUserIT extends ResourceTest {
    @Test
    public void testGetUser() {
        Response response = target("/v1/user").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        User fetched = response.readEntity(User.class);
        assertEquals("TWITTER:12345", fetched.getId());
        assertEquals("username", fetched.getUsername());
        assertEquals(TwitterClient, fetched.getSource());
        assertEquals("Display Name", fetched.getDisplayName());
    }
}
