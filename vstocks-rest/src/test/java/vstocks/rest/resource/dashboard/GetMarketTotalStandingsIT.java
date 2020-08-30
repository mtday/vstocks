package vstocks.rest.resource.dashboard;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.portfolio.MarketTotalRankService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetMarketTotalStandingsIT extends ResourceTest {
    @Test
    public void testGetStandingsNoAuthorizationHeader() {
        Response response = target("/dashboard/standings/market-total").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testGetStandingsNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/dashboard/standings/market-total").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testGetStandingsPage() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Page page = Page.from(2, 15, 0, 0);

        Results<RankedUser> results = new Results<RankedUser>().setPage(page).setResults(emptyList());
        MarketTotalRankService marketTotalRankService = mock(MarketTotalRankService.class);
        when(marketTotalRankService.getUsers(eq(page))).thenReturn(results);
        when(getServiceFactory().getMarketTotalRankService()).thenReturn(marketTotalRankService);

        Response response = target("/dashboard/standings/market-total")
                .queryParam("pageNum", page.getPage())
                .queryParam("pageSize", page.getSize())
                .queryParam("sort", "USER_ID,RANK:DESC")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"page\":{\"page\":2,\"size\":15,\"totalPages\":0,\"firstRow\":null,\"lastRow\":null,"
                + "\"totalRows\":0},\"results\":[]}", json);

        Results<RankedUser> fetched = convert(json, new RankedUserResultsTypeRef());
        assertEquals(results, fetched);
    }

    @Test
    public void testGetStandings() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        User user1 = new User()
                .setId("u1")
                .setEmail("user1@domain.com")
                .setUsername("user1")
                .setDisplayName("User1")
                .setProfileImage("link1");
        User user2 = new User()
                .setId("u2")
                .setEmail("user2@domain.com")
                .setUsername("user2")
                .setDisplayName("User2")
                .setProfileImage("link2");

        RankedUser rankedUser1 = new RankedUser()
                .setUser(user1)
                .setBatch(1)
                .setTimestamp(timestamp)
                .setRank(1)
                .setValue(10);
        RankedUser rankedUser2 = new RankedUser()
                .setUser(user2)
                .setBatch(1)
                .setTimestamp(timestamp)
                .setRank(2)
                .setValue(9);

        List<RankedUser> rankedUsers = asList(rankedUser1, rankedUser2);
        Results<RankedUser> results = new Results<RankedUser>().setPage(Page.from(1, 20, 2, 2)).setResults(rankedUsers);

        MarketTotalRankService marketTotalRankService = mock(MarketTotalRankService.class);
        when(marketTotalRankService.getUsers(any())).thenReturn(results);
        when(getServiceFactory().getMarketTotalRankService()).thenReturn(marketTotalRankService);

        Response response = target("/dashboard/standings/market-total").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String user1json = "{\"id\":\"u1\",\"email\":\"user1@domain.com\",\"username\":\"user1\","
                + "\"displayName\":\"User1\",\"profileImage\":\"link1\"}";
        String user2json = "{\"id\":\"u2\",\"email\":\"user2@domain.com\",\"username\":\"user2\","
                + "\"displayName\":\"User2\",\"profileImage\":\"link2\"}";
        String rankedUser1json = "{\"user\":" + user1json + ",\"batch\":1,\"timestamp\":\"2020-12-03T10:15:30Z\","
                + "\"rank\":1,\"value\":10}";
        String rankedUser2json = "{\"user\":" + user2json + ",\"batch\":1,\"timestamp\":\"2020-12-03T10:15:30Z\","
                + "\"rank\":2,\"value\":9}";
        String expectedJson = "{\"page\":{\"page\":1,\"size\":20,\"totalPages\":1,\"firstRow\":1,\"lastRow\":2,"
                + "\"totalRows\":2},\"results\":[" + rankedUser1json + "," + rankedUser2json + "]}";
        assertEquals(expectedJson, json);

        Results<RankedUser> fetched = convert(json, new RankedUserResultsTypeRef());
        assertEquals(results, fetched);
    }
}
