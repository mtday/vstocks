package vstocks.rest.resource.v1.user.achievement;

import org.junit.Test;
import vstocks.db.UserAchievementDB;
import vstocks.model.ErrorResponse;
import vstocks.model.UserAchievement;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GetUserAchievementsIT extends ResourceTest {
    @Test
    public void testGetUserAchievementsNoAuthorizationHeader() {
        Response response = target("/v1/user/achievements").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing JWT authorization bearer token", errorResponse.getMessage());
    }

    @Test
    public void testGetUserAchievementsNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/v1/user/achievements").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals("Missing JWT authorization bearer token", errorResponse.getMessage());
    }

    @Test
    public void testGetUserAchievementsNone() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser()));

        UserAchievementDB userAchievementDB = mock(UserAchievementDB.class);
        when(userAchievementDB.getForUser(eq(getUser().getId()))).thenReturn(emptyList());
        when(getDBFactory().getUserAchievementDB()).thenReturn(userAchievementDB);

        Response response = target("/v1/user/achievements").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertTrue(response.readEntity(new UserAchievementListGenericType()).isEmpty());

        verify(userAchievementDB, times(1)).getForUser(eq(getUser().getId()));
    }

    @Test
    public void testGetUserAchievementsSomeAvailable() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser()));

        UserAchievement userAchievement = new UserAchievement()
                .setUserId(getUser().getId())
                .setAchievementId("achievement-id")
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setDescription("description");

        UserAchievementDB userAchievementDB = mock(UserAchievementDB.class);
        when(userAchievementDB.getForUser(eq(getUser().getId()))).thenReturn(singletonList(userAchievement));
        when(getDBFactory().getUserAchievementDB()).thenReturn(userAchievementDB);

        Response response = target("/v1/user/achievements").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        List<UserAchievement> userAchievements = response.readEntity(new UserAchievementListGenericType());
        assertEquals(1, userAchievements.size());
        assertEquals(userAchievement.getUserId(), userAchievements.get(0).getUserId());
        assertEquals(userAchievement.getAchievementId(), userAchievements.get(0).getAchievementId());
        assertEquals(userAchievement.getTimestamp(), userAchievements.get(0).getTimestamp());
        assertEquals(userAchievement.getDescription(), userAchievements.get(0).getDescription());

        verify(userAchievementDB, times(1)).getForUser(eq(getUser().getId()));
    }
}