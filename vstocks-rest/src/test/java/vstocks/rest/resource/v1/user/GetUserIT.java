package vstocks.rest.resource.v1.user;

import org.junit.Test;
import vstocks.model.User;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;

public class GetUserIT extends ResourceTest {
    @Test
    public void testGetUser() {
        Response response = target("/v1/user").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        User user = getUser();
        User fetched = response.readEntity(User.class);
        assertEquals(user.getId(), fetched.getId());
        assertEquals(user.getEmail(), fetched.getEmail());
        assertEquals(user.getUsername(), fetched.getUsername());
        assertEquals(user.getDisplayName(), fetched.getDisplayName());
    }
}
