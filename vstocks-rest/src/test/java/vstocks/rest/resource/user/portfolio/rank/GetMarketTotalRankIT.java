package vstocks.rest.resource.user.portfolio.rank;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.portfolio.MarketTotalRankService;
import vstocks.model.Delta;
import vstocks.model.ErrorResponse;
import vstocks.model.portfolio.MarketTotalRank;
import vstocks.model.portfolio.MarketTotalRankCollection;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
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
import static vstocks.model.Delta.getDeltas;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetMarketTotalRankIT extends ResourceTest {
    @Test
    public void testUserPortfolioMarketTotalRankNoAuthorizationHeader() {
        Response response = target("/user/portfolio/rank/market-total").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioMarketTotalRankNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/rank/market-total")
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
    public void testUserPortfolioMarketTotalRankWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        MarketTotalRank marketTotalRank1 = new MarketTotalRank()
                .setBatch(2)
                .setUserId(getUser().getId())
                .setTimestamp(timestamp)
                .setRank(20)
                .setValue(10);
        MarketTotalRank marketTotalRank2 = new MarketTotalRank()
                .setBatch(1)
                .setUserId(getUser().getId())
                .setTimestamp(timestamp.minusSeconds(10))
                .setRank(18)
                .setValue(9);

        List<MarketTotalRank> ranks = asList(marketTotalRank1, marketTotalRank2);
        List<Delta> deltas = getDeltas(ranks, MarketTotalRank::getTimestamp, MarketTotalRank::getRank);
        MarketTotalRankCollection collection = new MarketTotalRankCollection().setRanks(ranks).setDeltas(deltas);

        MarketTotalRankService marketTotalRankService = mock(MarketTotalRankService.class);
        when(marketTotalRankService.getLatest(eq(getUser().getId()))).thenReturn(collection);
        when(getServiceFactory().getMarketTotalRankService()).thenReturn(marketTotalRankService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/rank/market-total")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String rank1json = "{\"batch\":2,\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"rank\":20,\"value\":10}";
        String rank2json = "{\"batch\":1,\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\","
                + "\"timestamp\":\"2020-12-03T10:15:20Z\",\"rank\":18,\"value\":9}";
        String deltajson = String.join(",", asList(
                "{\"interval\":\"6h\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"12h\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"1d\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"3d\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"7d\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"14d\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"30d\",\"change\":2,\"percent\":11.111112}"
        ));
        String expected = "{\"ranks\":[" + rank1json + "," + rank2json + "],\"deltas\":[" + deltajson + "]}";
        assertEquals(expected, json);

        MarketTotalRankCollection fetched = convert(json, MarketTotalRankCollection.class);
        assertEquals(collection, fetched);
    }
}
