package vstocks.rest.resource.security;

import org.junit.Test;
import org.mockito.ArgumentMatcher;
import vstocks.db.ActivityLogDB;
import vstocks.db.UserDB;
import vstocks.model.ActivityLog;
import vstocks.model.User;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.util.Objects;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.Response.Status.TEMPORARY_REDIRECT;
import static org.glassfish.jersey.client.ClientProperties.FOLLOW_REDIRECTS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static vstocks.model.ActivityType.USER_LOGIN;

public class LoginIT extends ResourceTest {
    @Test
    public void testLoginNoExistingUserNoExistingUsername() {
        for (String type : asList("facebook", "google", "twitter")) {
            testLoginNoExistingUserNoExistingUsername(type);
        }
    }

    private void testLoginNoExistingUserNoExistingUsername(String type) {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(empty());
        when(userDB.usernameExists(eq(getUser().getUsername()))).thenReturn(false);

        ActivityLogDB activityLogDB = mock(ActivityLogDB.class);

        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getDBFactory().getActivityLogDB()).thenReturn(activityLogDB);

        when(getJwtSecurity().generateToken(eq(getUser().getId()))).thenReturn("token");

        Response response = target("/security/login/" + type).request().property(FOLLOW_REDIRECTS, false).get();

        assertEquals(TEMPORARY_REDIRECT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
        assertEquals("/", response.getLocation().getPath());
        assertEquals("token=token", response.getLocation().getQuery());
        assertEquals("", response.readEntity(String.class));

        ActivityLog activityLog = new ActivityLog()
                .setUserId(getUser().getId())
                .setType(USER_LOGIN);

        verify(userDB, times(1)).add(argThat(new UserArgumentMatcher(getUser())));
        verify(activityLogDB, times(1)).add(argThat(new ActivityLogArgumentMatcher(activityLog)));
    }

    @Test
    public void testLoginNoExistingUserExistingUsernameConflict() {
        for (String type : asList("facebook", "google", "twitter")) {
            testLoginNoExistingUserExistingUsernameConflict(type);
        }
    }

    private void testLoginNoExistingUserExistingUsernameConflict(String type) {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(empty());
        when(userDB.usernameExists(eq(getUser().getUsername()))).thenReturn(true, false);

        ActivityLogDB activityLogDB = mock(ActivityLogDB.class);

        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getDBFactory().getActivityLogDB()).thenReturn(activityLogDB);

        when(getJwtSecurity().generateToken(eq(getUser().getId()))).thenReturn("token");

        Response response = target("/security/login/" + type).request().property(FOLLOW_REDIRECTS, false).get();

        assertEquals(TEMPORARY_REDIRECT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
        assertEquals("/", response.getLocation().getPath());
        assertEquals("token=token", response.getLocation().getQuery());
        assertEquals("", response.readEntity(String.class));

        ActivityLog activityLog = new ActivityLog()
                .setUserId(getUser().getId())
                .setType(USER_LOGIN);

        verify(userDB, times(1)).add(argThat(user ->
                getUser().getId().equals(user.getId()) && !getUser().getUsername().equals(user.getUsername())));
        verify(activityLogDB, times(1)).add(argThat(new ActivityLogArgumentMatcher(activityLog)));
    }

    @Test
    public void testLoginExistingUserDifferentProfileImage() {
        for (String type : asList("facebook", "google", "twitter")) {
            testLoginExistingUserDifferentProfileImage(type);
        }
    }

    private void testLoginExistingUserDifferentProfileImage(String type) {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId())))
                .thenReturn(Optional.of(getUser().setProfileImage("https://different/image.png")));

        ActivityLogDB activityLogDB = mock(ActivityLogDB.class);

        when(getDBFactory().getUserDB()).thenReturn(userDB);
        when(getDBFactory().getActivityLogDB()).thenReturn(activityLogDB);

        when(getJwtSecurity().generateToken(eq(getUser().getId()))).thenReturn("token");

        Response response = target("/security/login/" + type).request().property(FOLLOW_REDIRECTS, false).get();

        assertEquals(TEMPORARY_REDIRECT.getStatusCode(), response.getStatus());
        assertNull(response.getHeaderString(CONTENT_TYPE));
        assertEquals("/", response.getLocation().getPath());
        assertEquals("token=token", response.getLocation().getQuery());
        assertEquals("", response.readEntity(String.class));

        ActivityLog activityLog = new ActivityLog()
                .setUserId(getUser().getId())
                .setType(USER_LOGIN);

        verify(userDB, times(1)).update(argThat(user ->
                // image reset back to the one from the profile
                getUser().equals(user) && getUser().getProfileImage().equals(user.getProfileImage())));
        verify(activityLogDB, times(1)).add(argThat(new ActivityLogArgumentMatcher(activityLog)));
    }

    private static class UserArgumentMatcher implements org.mockito.ArgumentMatcher<User> {
        private final User expected;

        public UserArgumentMatcher(User expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(User user) {
            return Objects.equals(user.getId(), expected.getId())
                    && Objects.equals(user.getEmail(), expected.getEmail())
                    && Objects.equals(user.getUsername(), expected.getUsername())
                    && Objects.equals(user.getDisplayName(), expected.getDisplayName())
                    && Objects.equals(user.getProfileImage(), expected.getProfileImage());
        }

        @Override
        public String toString() {
            return expected.toString();
        }
    }

    private static class ActivityLogArgumentMatcher implements ArgumentMatcher<ActivityLog> {
        private final ActivityLog expected;

        public ActivityLogArgumentMatcher(ActivityLog expected) {
            this.expected = expected;
        }

        @Override
        public boolean matches(ActivityLog activityLog) {
            return Objects.equals(activityLog.getUserId(), expected.getUserId())
                    && Objects.equals(activityLog.getType(), expected.getType());
        }

        @Override
        public String toString() {
            return expected.toString();
        }
    }
}
