package vstocks.rest.resource.v1.user;

import org.junit.Test;
import vstocks.db.UserDB;
import vstocks.model.ErrorResponse;
import vstocks.rest.ResourceTest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserResetIT extends ResourceTest {
    @Test
    public void testResetUserNoAuthorizationHeader() {
        Response response = target("/v1/user/reset").request().put(Entity.text(""));

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing JWT authorization bearer token", errorResponse.getMessage());
    }

    @Test
    public void testResetUserNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/v1/user/reset").request().header(AUTHORIZATION, "Bearer token").put(Entity.text(""));

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing JWT authorization bearer token", errorResponse.getMessage());
    }

    @Test
    public void testResetUser() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/v1/user/reset").request().header(AUTHORIZATION, "Bearer token").put(Entity.text(""));
        assertEquals(OK.getStatusCode(), response.getStatus());

        verify(userDB, times(1)).reset(eq(getUser().getId()));
    }
}
