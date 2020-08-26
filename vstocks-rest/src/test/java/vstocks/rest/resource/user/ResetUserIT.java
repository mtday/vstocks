package vstocks.rest.resource.user;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.model.ErrorResponse;
import vstocks.model.UserReset;
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
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class ResetUserIT extends ResourceTest {
    @Test
    public void testResetUserNoAuthorizationHeader() {
        Response response = target("/user/reset").request().put(Entity.text(""));

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testResetUserNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/reset").request().header(AUTHORIZATION, "Bearer token").put(Entity.text(""));

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testResetUserSuccess() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        when(userService.reset(eq(getUser().getId()))).thenReturn(1);

        Response response = target("/user/reset").request().header(AUTHORIZATION, "Bearer token").put(Entity.text(""));
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"user\":{\"id\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"email\":\"user@domain.com\","
                + "\"username\":\"username\",\"displayName\":\"Display Name\","
                + "\"profileImage\":\"https://domain.com/user/profile-image.png\"},\"reset\":true}", json);

        UserReset userReset = convert(json, UserReset.class);
        assertEquals(getUser(), userReset.getUser());
        assertTrue(userReset.isReset());

        verify(userService, times(1)).reset(eq(getUser().getId()));
    }

    @Test
    public void testResetUserFail() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        when(userService.reset(eq(getUser().getId()))).thenReturn(0); // 0 means no update

        Response response = target("/user/reset").request().header(AUTHORIZATION, "Bearer token").put(Entity.text(""));
        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"user\":{\"id\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"email\":\"user@domain.com\","
                + "\"username\":\"username\",\"displayName\":\"Display Name\","
                + "\"profileImage\":\"https://domain.com/user/profile-image.png\"},\"reset\":false}", json);

        UserReset userReset = convert(json, UserReset.class);
        assertEquals(getUser(), userReset.getUser());
        assertFalse(userReset.isReset());

        verify(userService, times(1)).reset(eq(getUser().getId()));
    }
}
