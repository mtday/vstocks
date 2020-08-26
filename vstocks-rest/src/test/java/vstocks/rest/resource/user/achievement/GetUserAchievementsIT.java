package vstocks.rest.resource.user.achievement;

import org.junit.Test;
import vstocks.db.UserAchievementService;
import vstocks.db.UserService;
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
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetUserAchievementsIT extends ResourceTest {
    @Test
    public void testGetUserAchievementsNoAuthorizationHeader() {
        Response response = target("/user/achievements").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testGetUserAchievementsNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/achievements").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testGetUserAchievementsNone() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        UserAchievementService userAchievementService = mock(UserAchievementService.class);
        when(userAchievementService.getForUser(eq(getUser().getId()))).thenReturn(emptyList());
        when(getServiceFactory().getUserAchievementService()).thenReturn(userAchievementService);

        Response response = target("/user/achievements").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("[]", json);

        List<UserAchievement> fetched = convert(json, new UserAchievementListTypeRef());
        assertTrue(fetched.isEmpty());

        verify(userAchievementService, times(1)).getForUser(eq(getUser().getId()));
    }

    @Test
    public void testGetUserAchievementsSomeAvailable() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        UserAchievement userAchievement = new UserAchievement()
                .setUserId(getUser().getId())
                .setAchievementId("achievement-id")
                .setTimestamp(timestamp)
                .setDescription("description");

        UserAchievementService userAchievementService = mock(UserAchievementService.class);
        when(userAchievementService.getForUser(eq(getUser().getId()))).thenReturn(singletonList(userAchievement));
        when(getServiceFactory().getUserAchievementService()).thenReturn(userAchievementService);

        Response response = target("/user/achievements").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("[{\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"achievementId\":\"achievement-id\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"description\":\"description\"}]", json);

        List<UserAchievement> fetched = convert(json, new UserAchievementListTypeRef());
        assertEquals(1, fetched.size());
        assertEquals(userAchievement.getUserId(), fetched.get(0).getUserId());
        assertEquals(userAchievement.getAchievementId(), fetched.get(0).getAchievementId());
        assertEquals(userAchievement.getTimestamp(), fetched.get(0).getTimestamp());
        assertEquals(userAchievement.getDescription(), fetched.get(0).getDescription());

        verify(userAchievementService, times(1)).getForUser(eq(getUser().getId()));
    }
}
