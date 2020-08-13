package vstocks.rest.resource.v1.user;

import org.junit.Test;
import vstocks.db.UserDB;
import vstocks.model.ErrorResponse;
import vstocks.model.User;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.util.Optional;

import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetUserIT extends ResourceTest {
    @Test
    public void testGetUserNoAuthorizationHeader() {
        Response response = target("/v1/user").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing JWT authorization bearer token", errorResponse.getMessage());
    }

    @Test
    public void testGetUserNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/v1/user").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing JWT authorization bearer token", errorResponse.getMessage());
    }

    @Test
    public void testGetUser() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/v1/user").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        User user = getUser();
        User fetched = response.readEntity(User.class);
        assertEquals(user.getId(), fetched.getId());
        assertNull(fetched.getEmail()); // email not included in json responses
        assertEquals(user.getUsername(), fetched.getUsername());
        assertEquals(user.getDisplayName(), fetched.getDisplayName());
    }
}
