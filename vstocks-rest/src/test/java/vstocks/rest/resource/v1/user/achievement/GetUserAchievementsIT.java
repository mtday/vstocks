package vstocks.rest.resource.v1.user.achievement;

import org.junit.Test;
import vstocks.db.UserAchievementDB;
import vstocks.model.UserAchievement;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class GetUserAchievementsIT extends ResourceTest {
    @Test
    public void testGetUserAchievementsNone() {
        UserAchievementDB userAchievementDB = mock(UserAchievementDB.class);
        when(userAchievementDB.getForUser(eq(getUser().getId()))).thenReturn(emptyList());
        when(getDBFactory().getUserAchievementDB()).thenReturn(userAchievementDB);

        Response response = target("/v1/user/achievements").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertTrue(response.readEntity(new UserAchievementListGenericType()).isEmpty());

        verify(userAchievementDB, times(1)).getForUser(eq(getUser().getId()));
    }

    @Test
    public void testGetUserAchievementsSomeAvailable() {
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(getUser().getId())
                .setAchievementId("achievement-id")
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setDescription("description");

        UserAchievementDB userAchievementDB = mock(UserAchievementDB.class);
        when(userAchievementDB.getForUser(eq(getUser().getId()))).thenReturn(singletonList(userAchievement));
        when(getDBFactory().getUserAchievementDB()).thenReturn(userAchievementDB);

        Response response = target("/v1/user/achievements").request().get();

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
