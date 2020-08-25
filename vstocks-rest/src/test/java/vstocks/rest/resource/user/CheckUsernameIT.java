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

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
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

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUsernameExists() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userService.usernameExists(eq("username"))).thenReturn(true);
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/check")
                .queryParam("username", "username")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        UsernameCheck usernameCheck = response.readEntity(UsernameCheck.class);
        assertEquals("username", usernameCheck.getUsername());
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

        UsernameCheck usernameCheck = response.readEntity(UsernameCheck.class);
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

        UsernameCheck usernameCheck = response.readEntity(UsernameCheck.class);
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

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
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

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
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

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing or invalid username parameter", errorResponse.getMessage());
    }
}
