package vstocks.rest.resource.standings;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.portfolio.TotalRankService;
import vstocks.model.ErrorResponse;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.User;
import vstocks.model.portfolio.RankedUser;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetTotalStandingsIT extends ResourceTest {
    @Test
    public void testGetStandingsNoAuthorizationHeader() {
        Response response = target("/standings/total").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testGetStandingsNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/standings/total").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testGetStandingsPage() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Page page = new Page().setPage(2).setSize(15);

        Results<RankedUser> results = new Results<RankedUser>().setTotal(0).setPage(page).setResults(emptyList());
        TotalRankService totalRankService = mock(TotalRankService.class);
        when(totalRankService.getUsers(eq(page))).thenReturn(results);
        when(getServiceFactory().getTotalRankService()).thenReturn(totalRankService);

        Response response = target("/standings/total")
                .queryParam("pageNum", page.getPage())
                .queryParam("pageSize", page.getSize())
                .queryParam("sort", "USER_ID,RANK:DESC")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(results, response.readEntity(new RankedUserResultsGenericType()));
    }

    @Test
    public void testGetStandings() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Instant now = Instant.now().truncatedTo(SECONDS);
        RankedUser rankedUser1 = new RankedUser()
                .setUser(new User().setId("u1").setEmail("user1@domain.com").setUsername("user1"))
                .setBatch(1)
                .setTimestamp(now)
                .setRank(1)
                .setValue(10);
        RankedUser rankedUser2 = new RankedUser()
                .setUser(new User().setId("u2").setEmail("user2@domain.com").setUsername("user2"))
                .setBatch(1)
                .setTimestamp(now)
                .setRank(2)
                .setValue(9);

        List<RankedUser> rankedUsers = asList(rankedUser1, rankedUser2);
        Results<RankedUser> results = new Results<RankedUser>().setTotal(2).setPage(new Page()).setResults(rankedUsers);

        TotalRankService totalRankService = mock(TotalRankService.class);
        when(totalRankService.getUsers(eq(new Page()))).thenReturn(results);
        when(getServiceFactory().getTotalRankService()).thenReturn(totalRankService);

        Response response = target("/standings/total").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(results, response.readEntity(new RankedUserResultsGenericType()));
    }
}
