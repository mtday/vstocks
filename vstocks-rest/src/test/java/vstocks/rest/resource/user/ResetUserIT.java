package vstocks.rest.resource.user;

import org.junit.Test;
import vstocks.db.UserService;
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
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class ResetUserIT extends ResourceTest {
    @Test
    public void testResetUserNoAuthorizationHeader() {
        Response response = target("/user/reset").request().put(Entity.text(""));

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testResetUserNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/reset").request().header(AUTHORIZATION, "Bearer token").put(Entity.text(""));

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testResetUser() {
        UserService userDB = mock(UserService.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserService()).thenReturn(userDB);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/reset").request().header(AUTHORIZATION, "Bearer token").put(Entity.text(""));
        assertEquals(OK.getStatusCode(), response.getStatus());

        verify(userDB, times(1)).reset(eq(getUser().getId()));
    }
}
