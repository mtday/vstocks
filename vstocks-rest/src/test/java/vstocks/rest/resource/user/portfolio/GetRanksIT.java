package vstocks.rest.resource.user.portfolio;

import org.junit.Test;
import vstocks.db.PortfolioValueRankDB;
import vstocks.db.UserDB;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.ErrorResponse;
import vstocks.model.PortfolioValueRank;
import vstocks.model.rest.UserPortfolioRankResponse;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetRanksIT extends ResourceTest {
    @Test
    public void testUserPortfolioRanksNoAuthorizationHeader() {
        Response response = target("/user/portfolio/ranks").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioRanksNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/ranks").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioRanksNoData() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);

        PortfolioValueRankDB portfolioValueRankDB = mock(PortfolioValueRankDB.class);
        when(portfolioValueRankDB.getForUserSince(eq(getUser().getId()), any(), any())).thenReturn(emptyList());
        when(getDBFactory().getPortfolioValueRankDB()).thenReturn(portfolioValueRankDB);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/ranks").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        UserPortfolioRankResponse userPortfolioRankResponse = response.readEntity(UserPortfolioRankResponse.class);
        assertNull(userPortfolioRankResponse.getCurrentRank());
        assertTrue(userPortfolioRankResponse.getHistoricalRanks().isEmpty());
        assertEquals(DeltaInterval.values().length, userPortfolioRankResponse.getDeltas().size());
    }

    @Test
    public void testUserPortfolioRanksWithData() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);

        List<PortfolioValueRank> ranks = Stream.iterate(1, i -> i + 1).limit(10)
                .map(i -> new PortfolioValueRank()
                        .setUserId(getUser().getId())
                        .setRank(i)
                        .setTimestamp(Instant.now().minusSeconds(i * 10).truncatedTo(SECONDS)))
                .collect(toList());

        PortfolioValueRankDB portfolioValueRankDB = mock(PortfolioValueRankDB.class);
        when(portfolioValueRankDB.getForUserSince(eq(getUser().getId()), any(), any())).thenReturn(ranks);
        when(getDBFactory().getPortfolioValueRankDB()).thenReturn(portfolioValueRankDB);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/ranks").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        UserPortfolioRankResponse userPortfolioRankResponse = response.readEntity(UserPortfolioRankResponse.class);

        // make sure the current value rank is correct
        PortfolioValueRank expectedCurrentRank = ranks.iterator().next();
        assertEquals(expectedCurrentRank.getUserId(), userPortfolioRankResponse.getCurrentRank().getUserId());
        assertEquals(expectedCurrentRank.getRank(), userPortfolioRankResponse.getCurrentRank().getRank());
        assertEquals(expectedCurrentRank.getTimestamp(), userPortfolioRankResponse.getCurrentRank().getTimestamp());

        // make sure the historical values are correct
        assertEquals(ranks.size(), userPortfolioRankResponse.getHistoricalRanks().size());
        Iterator<PortfolioValueRank> expectedRankIter = ranks.iterator();
        Iterator<PortfolioValueRank> actualRankIter = userPortfolioRankResponse.getHistoricalRanks().iterator();
        while (expectedRankIter.hasNext() && actualRankIter.hasNext()) {
            PortfolioValueRank expected = expectedRankIter.next();
            PortfolioValueRank actual = actualRankIter.next();
            assertEquals(expected.getUserId(), actual.getUserId());
            assertEquals(expected.getRank(), actual.getRank());
            assertEquals(expected.getTimestamp(), actual.getTimestamp());
        }

        assertEquals(DeltaInterval.values().length, userPortfolioRankResponse.getDeltas().size());
        Arrays.stream(DeltaInterval.values()).forEach(interval -> {
            Delta delta = new Delta().setInterval(interval).setChange(-9).setPercent(-90f);
            assertEquals(delta, userPortfolioRankResponse.getDeltas().get(interval));
        });
    }
}
