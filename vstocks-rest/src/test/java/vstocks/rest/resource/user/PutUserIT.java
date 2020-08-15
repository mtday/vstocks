package vstocks.rest.resource.user;

import org.junit.Test;
import org.mockito.stubbing.Answer;
import vstocks.db.UserDB;
import vstocks.model.ErrorResponse;
import vstocks.model.User;
import vstocks.rest.ResourceTest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class PutUserIT extends ResourceTest {
    @Test
    public void testPutUserNoAuthorizationHeader() {
        Entity<User> entity = Entity.entity(getUser(), APPLICATION_JSON);
        Response response = target("/user").request().put(entity);

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testPutUserNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Entity<User> entity = Entity.entity(getUser(), APPLICATION_JSON);
        Response response = target("/user").request().header(AUTHORIZATION, "Bearer token").put(entity);

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testPutUserNoChange() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenAnswer((Answer<Optional<User>>) invocation -> Optional.of(getUser()));
        when(userDB.update(any())).thenReturn(0); // 0 means db was not updated
        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        User updateUser = new User()
                .setEmail("updated")
                .setUsername("updated")
                .setDisplayName("updated")
                .setImageLink("updated");
        Entity<User> entity = Entity.entity(updateUser, APPLICATION_JSON);
        Response response = target("/user").request().header(AUTHORIZATION, "Bearer token").put(entity);

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        // None of the fields have been updated, the original user from the db was returned
        User user = getUser();
        User fetched = response.readEntity(User.class);
        assertEquals(user.getId(), fetched.getId());
        assertNull(fetched.getEmail()); // email not included in json responses
        assertEquals(user.getUsername(), fetched.getUsername());
        assertEquals(user.getDisplayName(), fetched.getDisplayName());
        assertEquals(user.getImageLink(), fetched.getImageLink());

        verify(userDB, times(1)).update(argThat(update -> {
            return Objects.equals(user.getId(), update.getId()) // not updated
                    && Objects.equals(user.getEmail(), update.getEmail()) // not updated
                    && Objects.equals(updateUser.getUsername(), update.getUsername()) // updated
                    && Objects.equals(updateUser.getDisplayName(), update.getDisplayName()) // updated
                    && Objects.equals(user.getImageLink(), update.getImageLink()); // not updated
        }));
    }

    @Test
    public void testPutUserChanged() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userDB.update(any())).thenReturn(1); // 1 means db was updated
        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        User updateUser = new User()
                .setEmail("updated")
                .setUsername("updated")
                .setDisplayName("updated")
                .setImageLink("updated");
        Entity<User> entity = Entity.entity(updateUser, APPLICATION_JSON);
        Response response = target("/user").request().header(AUTHORIZATION, "Bearer token").put(entity);

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        // Some of the fields have been updated, the updated user was returned
        User user = getUser();
        User fetched = response.readEntity(User.class);
        assertEquals(user.getId(), fetched.getId()); // still original value
        assertNull(fetched.getEmail()); // email not included in json responses
        assertEquals(updateUser.getUsername(), fetched.getUsername()); // updated value
        assertEquals(updateUser.getDisplayName(), fetched.getDisplayName()); // updated value
        assertEquals(user.getImageLink(), fetched.getImageLink()); // still original value

        verify(userDB, times(1)).update(argThat(update -> {
            return Objects.equals(user.getId(), update.getId()) // not updated
                    && Objects.equals(user.getEmail(), update.getEmail()) // not updated
                    && Objects.equals(updateUser.getUsername(), update.getUsername()) // updated
                    && Objects.equals(updateUser.getDisplayName(), update.getDisplayName()) // updated
                    && Objects.equals(user.getImageLink(), update.getImageLink()); // not updated
        }));
    }
}
