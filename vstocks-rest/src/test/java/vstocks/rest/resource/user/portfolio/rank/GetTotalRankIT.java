package vstocks.rest.resource.user.portfolio.rank;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.portfolio.TotalRankService;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.ErrorResponse;
import vstocks.model.portfolio.TotalRank;
import vstocks.model.portfolio.TotalRankCollection;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
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

public class GetTotalRankIT extends ResourceTest {
    @Test
    public void testUserPortfolioTotalRankNoAuthorizationHeader() {
        Response response = target("/user/portfolio/rank/total").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioTotalRankNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/rank/total")
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
    public void testUserPortfolioTotalRankWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");
        TotalRank totalRank1 = new TotalRank()
                .setBatch(1)
                .setUserId(getUser().getId())
                .setTimestamp(timestamp)
                .setRank(20);
        TotalRank totalRank2 = new TotalRank()
                .setBatch(1)
                .setUserId(getUser().getId())
                .setTimestamp(timestamp.minusSeconds(10))
                .setRank(18);

        List<TotalRank> ranks = asList(totalRank1, totalRank2);
        Map<DeltaInterval, Delta> deltas = Delta.getDeltas(ranks, TotalRank::getTimestamp, TotalRank::getRank);
        TotalRankCollection collection = new TotalRankCollection().setRanks(ranks).setDeltas(deltas);

        TotalRankService totalRankService = mock(TotalRankService.class);
        when(totalRankService.getLatest(eq(getUser().getId()))).thenReturn(collection);
        when(getServiceFactory().getTotalRankService()).thenReturn(totalRankService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/rank/total")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(collection, response.readEntity(TotalRankCollection.class));
    }
}
