package vstocks.rest.resource.user;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.model.ErrorResponse;
import vstocks.model.UsernameCheck;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.util.Optional;

import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.rest.resource.BaseResource.INVALID_USERNAME_MESSAGE;
import static vstocks.rest.resource.BaseResource.USERNAME_EXISTS_MESSAGE;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class CheckUsernameIT extends ResourceTest {
    @Test
    public void testUsernameExistsNoAuthorizationHeader() {
        Response response = target("/user/check").queryParam("username", "username").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUsernameExistsNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/check")
                .queryParam("username", "username")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUsernameExistsMatchesCurrentUser() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/check")
                .queryParam("username", getUser().getUsername())
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"username\":\"username\",\"exists\":false,\"valid\":true,\"message\":null}", json);

        UsernameCheck usernameCheck = convert(json, UsernameCheck.class);
        assertEquals("username", usernameCheck.getUsername());
        assertFalse(usernameCheck.isExists()); // it technically exists, but we return false anyway
        assertTrue(usernameCheck.isValid());
        assertNull(usernameCheck.getMessage());
    }

    @Test
    public void testUsernameExists() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userService.usernameExists(eq("existing"))).thenReturn(true);
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/check")
                .queryParam("username", "existing")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"username\":\"existing\",\"exists\":true,\"valid\":true,"
                + "\"message\":\"The specified username is already taken.\"}", json);

        UsernameCheck usernameCheck = convert(json, UsernameCheck.class);
        assertEquals("existing", usernameCheck.getUsername());
        assertTrue(usernameCheck.isExists());
        assertTrue(usernameCheck.isValid());
        assertEquals(USERNAME_EXISTS_MESSAGE, usernameCheck.getMessage());
    }

    @Test
    public void testUsernameDoesNotExist() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userService.usernameExists(eq("username"))).thenReturn(false);
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/check")
                .queryParam("username", "username")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"username\":\"username\",\"exists\":false,\"valid\":true,\"message\":null}", json);

        UsernameCheck usernameCheck = convert(json, UsernameCheck.class);
        assertEquals("username", usernameCheck.getUsername());
        assertFalse(usernameCheck.isExists());
        assertTrue(usernameCheck.isValid());
        assertNull(usernameCheck.getMessage());
    }

    @Test
    public void testUsernameInvalid() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userService.usernameExists(eq("user<>name"))).thenReturn(false);
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/check")
                .queryParam("username", "user<>name")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"username\":\"user<>name\",\"exists\":false,\"valid\":false,\"message\":\"The specified "
                + "username contains invalid characters. Only alphanumeric characters, along with underscores and "
                + "dashes are allowed.\"}", json);

        UsernameCheck usernameCheck = convert(json, UsernameCheck.class);
        assertEquals("user<>name", usernameCheck.getUsername());
        assertFalse(usernameCheck.isExists());
        assertFalse(usernameCheck.isValid());
        assertEquals(INVALID_USERNAME_MESSAGE, usernameCheck.getMessage());
    }

    @Test
    public void testNoQueryParam() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userService.usernameExists(eq("username"))).thenReturn(false);
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/check")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":400,\"message\":\"Missing or invalid username parameter\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing or invalid username parameter", errorResponse.getMessage());
    }

    @Test
    public void testEmptyQueryParam() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userService.usernameExists(eq("username"))).thenReturn(false);
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/check")
                .queryParam("username", "")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":400,\"message\":\"Missing or invalid username parameter\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing or invalid username parameter", errorResponse.getMessage());
    }

    @Test
    public void testWhiteSpaceQueryParam() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userService.usernameExists(eq("username"))).thenReturn(false);
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/check")
                .queryParam("username", " ")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":400,\"message\":\"Missing or invalid username parameter\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing or invalid username parameter", errorResponse.getMessage());
    }
}
