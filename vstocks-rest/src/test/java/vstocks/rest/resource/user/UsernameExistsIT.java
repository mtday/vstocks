package vstocks.rest.resource.user;

import org.junit.Test;
import vstocks.db.UserDB;
import vstocks.model.ErrorResponse;
import vstocks.rest.ResourceTest;
import vstocks.rest.resource.user.UsernameExists.UsernameExistsResponse;

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
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class UsernameExistsIT extends ResourceTest {
    @Test
    public void testUsernameExistsNoAuthorizationHeader() {
        Response response = target("/user/exists").request().property("username", "username").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUsernameExistsNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/exists")
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
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userDB.usernameExists(eq("username"))).thenReturn(true);
        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/exists")
                .queryParam("username", "username")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        UsernameExistsResponse usernameExistsResponse = response.readEntity(UsernameExistsResponse.class);
        assertEquals("username", usernameExistsResponse.getUsername());
        assertTrue(usernameExistsResponse.isExists());
    }

    @Test
    public void testUsernameDoesNotExist() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userDB.usernameExists(eq("username"))).thenReturn(false);
        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/exists")
                .queryParam("username", "username")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        UsernameExistsResponse usernameExistsResponse = response.readEntity(UsernameExistsResponse.class);
        assertEquals("username", usernameExistsResponse.getUsername());
        assertFalse(usernameExistsResponse.isExists());
    }

    @Test
    public void testNoQueryParam() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userDB.usernameExists(eq("username"))).thenReturn(false);
        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/exists")
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
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userDB.usernameExists(eq("username"))).thenReturn(false);
        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/exists")
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
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userDB.usernameExists(eq("username"))).thenReturn(false);
        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/exists")
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
