package vstocks.rest.resource.user;

import org.junit.Test;
import org.mockito.stubbing.Answer;
import vstocks.db.UserService;
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
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static vstocks.rest.resource.user.PutUser.INVALID_DISPLAY_NAME_MESSAGE;
import static vstocks.rest.resource.user.PutUser.INVALID_USERNAME_MESSAGE;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class PutUserIT extends ResourceTest {
    @Test
    public void testPutUserNoAuthorizationHeader() {
        Entity<User> entity = Entity.entity(getUser(), APPLICATION_JSON);
        Response response = target("/user").request().put(entity);

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
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

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testPutUserNoChange() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenAnswer((Answer<Optional<User>>) invocation -> Optional.of(getUser()));
        when(userService.update(any())).thenReturn(0); // 0 means db was not updated
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        User updateUser = new User()
                .setEmail("updated")
                .setUsername("updated")
                .setDisplayName("updated")
                .setProfileImage("updated");
        Entity<User> entity = Entity.entity(updateUser, APPLICATION_JSON);
        Response response = target("/user").request().header(AUTHORIZATION, "Bearer token").put(entity);

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"id\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"email\":\"user@domain.com\","
                + "\"username\":\"username\",\"displayName\":\"Display Name\","
                + "\"profileImage\":\"https://domain.com/user/profile-image.png\"}", json);

        // None of the fields have been updated, the original user from the db was returned
        User user = getUser();
        User fetched = convert(json, User.class);
        assertEquals(user, fetched);

        verify(userService, times(1)).update(argThat(update -> {
            return Objects.equals(user.getId(), update.getId()) // not updated
                    && Objects.equals(user.getEmail(), update.getEmail()) // not updated
                    && Objects.equals(updateUser.getUsername(), update.getUsername()) // updated
                    && Objects.equals(updateUser.getDisplayName(), update.getDisplayName()) // updated
                    && Objects.equals(user.getProfileImage(), update.getProfileImage()); // not updated
        }));
    }

    @Test
    public void testPutUserChanged() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userService.update(any())).thenReturn(1); // 1 means db was updated
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        User updateUser = new User()
                .setEmail("updated")
                .setUsername("updated")
                .setDisplayName("updated")
                .setProfileImage("updated");
        Entity<User> entity = Entity.entity(updateUser, APPLICATION_JSON);
        Response response = target("/user").request().header(AUTHORIZATION, "Bearer token").put(entity);

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"id\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"email\":\"user@domain.com\","
                + "\"username\":\"updated\",\"displayName\":\"updated\","
                + "\"profileImage\":\"https://domain.com/user/profile-image.png\"}", json);

        // Some of the fields have been updated, the updated user was returned
        User user = getUser();
        User fetched = convert(json, User.class);
        assertEquals(user.getId(), fetched.getId()); // still original value
        assertEquals(user.getEmail(), fetched.getEmail()); // still original value
        assertEquals(updateUser.getUsername(), fetched.getUsername()); // updated value
        assertEquals(updateUser.getDisplayName(), fetched.getDisplayName()); // updated value
        assertEquals(user.getProfileImage(), fetched.getProfileImage()); // still original value

        verify(userService, times(1)).update(argThat(update -> {
            return Objects.equals(user.getId(), update.getId()) // not updated
                    && Objects.equals(user.getEmail(), update.getEmail()) // not updated
                    && Objects.equals(updateUser.getUsername(), update.getUsername()) // updated
                    && Objects.equals(updateUser.getDisplayName(), update.getDisplayName()) // updated
                    && Objects.equals(user.getProfileImage(), update.getProfileImage()); // not updated
        }));
    }

    @Test
    public void testPutUserInvalidDisplayName() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userService.update(any())).thenReturn(1); // 1 means db was updated
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        User updateUser = new User()
                .setEmail("updated")
                .setUsername("updated")
                .setDisplayName("Invalid<>")
                .setProfileImage("updated");
        Entity<User> entity = Entity.entity(updateUser, APPLICATION_JSON);
        Response response = target("/user").request().header(AUTHORIZATION, "Bearer token").put(entity);

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":400,\"message\":\"The specified name contains invalid characters. Only alphanumeric "
                + "characters, along with underscores, dashes, and single quote characters are allowed.\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_DISPLAY_NAME_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testPutUserInvalidDisplayNameMatchesCurrentDisplayName() {
        User user = getUser();
        user.setDisplayName("Invalid<>");

        UserService userService = mock(UserService.class);
        when(userService.get(eq(user.getId()))).thenReturn(Optional.of(user));
        when(userService.update(any())).thenReturn(1); // 1 means db was updated
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(user.getId()));

        User updateUser = new User()
                .setEmail("updated")
                .setUsername("updated")
                .setDisplayName(user.getDisplayName())
                .setProfileImage("updated");
        Entity<User> entity = Entity.entity(updateUser, APPLICATION_JSON);
        Response response = target("/user").request().header(AUTHORIZATION, "Bearer token").put(entity);

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"id\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"email\":\"user@domain.com\","
                + "\"username\":\"updated\",\"displayName\":\"Invalid<>\","
                + "\"profileImage\":\"https://domain.com/user/profile-image.png\"}", json);

        // Some of the fields have been updated, the updated user was returned
        User fetched = convert(json, User.class);
        assertEquals(user.getId(), fetched.getId()); // still original value
        assertEquals(user.getEmail(), fetched.getEmail()); // still original value
        assertEquals(updateUser.getUsername(), fetched.getUsername()); // updated value
        assertEquals(updateUser.getDisplayName(), fetched.getDisplayName()); // still the original invalid value
        assertEquals(user.getProfileImage(), fetched.getProfileImage()); // still original value

        verify(userService, times(1)).update(argThat(update -> {
            return Objects.equals(user.getId(), update.getId()) // not updated
                    && Objects.equals(user.getEmail(), update.getEmail()) // not updated
                    && Objects.equals(updateUser.getUsername(), update.getUsername()) // updated
                    && Objects.equals(user.getDisplayName(), update.getDisplayName()) // still the original invalid value
                    && Objects.equals(user.getProfileImage(), update.getProfileImage()); // not updated
        }));
    }

    @Test
    public void testPutUserInvalidUsername() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(userService.update(any())).thenReturn(1); // 1 means db was updated
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        User updateUser = new User()
                .setEmail("updated")
                .setUsername("Invalid<>")
                .setDisplayName("updated")
                .setProfileImage("updated");
        Entity<User> entity = Entity.entity(updateUser, APPLICATION_JSON);
        Response response = target("/user").request().header(AUTHORIZATION, "Bearer token").put(entity);

        assertEquals(BAD_REQUEST.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":400,\"message\":\"The specified username contains invalid characters. Only "
                + "alphanumeric characters, along with underscores and dashes are allowed.\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(BAD_REQUEST.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_USERNAME_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testPutUserInvalidUsernameMatchesCurrentUsername() {
        User user = getUser();
        user.setUsername("Invalid<>");

        UserService userService = mock(UserService.class);
        when(userService.get(eq(user.getId()))).thenReturn(Optional.of(user));
        when(userService.update(any())).thenReturn(1); // 1 means db was updated
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(user.getId()));

        User updateUser = new User()
                .setEmail("updated")
                .setUsername(user.getUsername())
                .setDisplayName("updated")
                .setProfileImage("updated");
        Entity<User> entity = Entity.entity(updateUser, APPLICATION_JSON);
        Response response = target("/user").request().header(AUTHORIZATION, "Bearer token").put(entity);

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"id\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"email\":\"user@domain.com\","
                + "\"username\":\"Invalid<>\",\"displayName\":\"updated\","
                + "\"profileImage\":\"https://domain.com/user/profile-image.png\"}", json);

        // Some of the fields have been updated, the updated user was returned
        User fetched = convert(json, User.class);
        assertEquals(user.getId(), fetched.getId()); // still original value
        assertEquals(user.getEmail(), fetched.getEmail()); // still original value
        assertEquals(user.getUsername(), fetched.getUsername()); // still the original invalid value
        assertEquals(updateUser.getDisplayName(), fetched.getDisplayName()); // updated value
        assertEquals(user.getProfileImage(), fetched.getProfileImage()); // still original value

        verify(userService, times(1)).update(argThat(update -> {
            return Objects.equals(user.getId(), update.getId()) // not updated
                    && Objects.equals(user.getEmail(), update.getEmail()) // not updated
                    && Objects.equals(user.getUsername(), update.getUsername()) // still the original invalid value
                    && Objects.equals(updateUser.getDisplayName(), update.getDisplayName()) // updated
                    && Objects.equals(user.getProfileImage(), update.getProfileImage()); // not updated
        }));
    }
}
