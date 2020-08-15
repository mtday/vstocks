package vstocks.rest.resource.security;

import org.junit.Test;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.NO_CONTENT;
import static org.junit.Assert.assertEquals;

public class LogoutIT extends ResourceTest {
    @Test
    public void test() {
        Response response = target("/security/logout").request().get();
        assertEquals(NO_CONTENT.getStatusCode(), response.getStatus());
    }
}
