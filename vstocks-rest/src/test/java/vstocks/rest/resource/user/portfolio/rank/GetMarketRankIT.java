package vstocks.rest.resource.user.portfolio.rank;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.portfolio.MarketRankService;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.ErrorResponse;
import vstocks.model.portfolio.MarketRank;
import vstocks.model.portfolio.MarketRankCollection;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
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
import static vstocks.model.Market.TWITTER;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetMarketRankIT extends ResourceTest {
    @Test
    public void testUserPortfolioMarketRankNoAuthorizationHeader() {
        Response response = target("/user/portfolio/rank/market/TWITTER").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioMarketRankNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/rank/market/TWITTER")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioMarketRankWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        MarketRank marketRank1 = new MarketRank()
                .setBatch(2)
                .setUserId(getUser().getId())
                .setMarket(TWITTER)
                .setTimestamp(timestamp)
                .setRank(20)
                .setValue(10);
        MarketRank marketRank2 = new MarketRank()
                .setBatch(1)
                .setUserId(getUser().getId())
                .setMarket(TWITTER)
                .setTimestamp(timestamp.minusSeconds(10))
                .setRank(18)
                .setValue(9);

        List<MarketRank> ranks = asList(marketRank1, marketRank2);
        Map<DeltaInterval, Delta> deltas = Delta.getDeltas(ranks, MarketRank::getTimestamp, MarketRank::getRank);
        MarketRankCollection collection = new MarketRankCollection().setRanks(ranks).setDeltas(deltas);

        MarketRankService marketRankService = mock(MarketRankService.class);
        when(marketRankService.getLatest(eq(getUser().getId()), eq(TWITTER))).thenReturn(collection);
        when(getServiceFactory().getMarketRankService()).thenReturn(marketRankService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/rank/market/TWITTER")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String rank1json = "{\"batch\":2,\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"market\":\"Twitter\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"rank\":20,\"value\":10}";
        String rank2json = "{\"batch\":1,\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"market\":\"Twitter\","
                + "\"timestamp\":\"2020-12-03T10:15:20Z\",\"rank\":18,\"value\":9}";
        String deltajson = String.join(",", asList(
                "\"6h\":{\"interval\":\"6h\",\"change\":2,\"percent\":11.111112}",
                "\"12h\":{\"interval\":\"12h\",\"change\":2,\"percent\":11.111112}",
                "\"1d\":{\"interval\":\"1d\",\"change\":2,\"percent\":11.111112}",
                "\"3d\":{\"interval\":\"3d\",\"change\":2,\"percent\":11.111112}",
                "\"7d\":{\"interval\":\"7d\",\"change\":2,\"percent\":11.111112}",
                "\"14d\":{\"interval\":\"14d\",\"change\":2,\"percent\":11.111112}",
                "\"30d\":{\"interval\":\"30d\",\"change\":2,\"percent\":11.111112}"
        ));
        String expected = "{\"ranks\":[" + rank1json + "," + rank2json + "],\"deltas\":{" + deltajson + "}}";
        assertEquals(expected, json);

        MarketRankCollection fetched = convert(json, MarketRankCollection.class);
        assertEquals(collection, fetched);
    }
}
