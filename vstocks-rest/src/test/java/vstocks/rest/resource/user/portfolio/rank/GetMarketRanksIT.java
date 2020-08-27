package vstocks.rest.resource.user.portfolio.rank;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.portfolio.MarketRankService;
import vstocks.model.Delta;
import vstocks.model.ErrorResponse;
import vstocks.model.portfolio.MarketRank;
import vstocks.model.portfolio.MarketRankCollection;
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
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetMarketRanksIT extends ResourceTest {
    @Test
    public void testUserPortfolioMarketRanksNoAuthorizationHeader() {
        Response response = target("/user/portfolio/rank/markets").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioMarketRanksNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/rank/markets")
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
    public void testUserPortfolioMarketRanksWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        MarketRank twitterMarketRank1 = new MarketRank()
                .setBatch(2)
                .setUserId(getUser().getId())
                .setMarket(TWITTER)
                .setTimestamp(timestamp)
                .setRank(20)
                .setValue(10);
        MarketRank twitterMarketRank2 = new MarketRank()
                .setBatch(1)
                .setUserId(getUser().getId())
                .setMarket(TWITTER)
                .setTimestamp(timestamp.minusSeconds(10))
                .setRank(18)
                .setValue(9);

        List<MarketRank> twitterRanks = asList(twitterMarketRank1, twitterMarketRank2);
        List<Delta> twitterDeltas = getDeltas(twitterRanks, MarketRank::getTimestamp, MarketRank::getRank);
        MarketRankCollection twitterCollection =
                new MarketRankCollection().setMarket(TWITTER).setRanks(twitterRanks).setDeltas(twitterDeltas);

        MarketRank youtubeMarketRank1 = new MarketRank()
                .setBatch(2)
                .setUserId(getUser().getId())
                .setMarket(YOUTUBE)
                .setTimestamp(timestamp)
                .setRank(21)
                .setValue(11);
        MarketRank youtubeMarketRank2 = new MarketRank()
                .setBatch(1)
                .setUserId(getUser().getId())
                .setMarket(YOUTUBE)
                .setTimestamp(timestamp.minusSeconds(10))
                .setRank(19)
                .setValue(10);

        List<MarketRank> youtubeRanks = asList(youtubeMarketRank1, youtubeMarketRank2);
        List<Delta> youtubeDeltas = getDeltas(youtubeRanks, MarketRank::getTimestamp, MarketRank::getRank);
        MarketRankCollection youtubeCollection =
                new MarketRankCollection().setMarket(YOUTUBE).setRanks(youtubeRanks).setDeltas(youtubeDeltas);

        List<MarketRankCollection> collections = asList(twitterCollection, youtubeCollection);

        MarketRankService marketRankService = mock(MarketRankService.class);
        when(marketRankService.getLatest(eq(getUser().getId()))).thenReturn(collections);
        when(getServiceFactory().getMarketRankService()).thenReturn(marketRankService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/rank/markets")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String twitterRank1json = "{\"batch\":2,\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\","
                + "\"market\":\"Twitter\",\"timestamp\":\"2020-12-03T10:15:30Z\",\"rank\":20,\"value\":10}";
        String twitterRank2json = "{\"batch\":1,\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\","
                + "\"market\":\"Twitter\",\"timestamp\":\"2020-12-03T10:15:20Z\",\"rank\":18,\"value\":9}";
        String twitterDeltajson = String.join(",", asList(
                "{\"interval\":\"6h\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"12h\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"1d\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"3d\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"7d\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"14d\",\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"30d\",\"change\":2,\"percent\":11.111112}"
        ));
        String twitterExpected = "{\"market\":\"Twitter\",\"ranks\":[" + twitterRank1json + "," + twitterRank2json
                + "],\"deltas\":[" + twitterDeltajson + "]}";

        String youtubeRank1json = "{\"batch\":2,\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\","
                + "\"market\":\"YouTube\",\"timestamp\":\"2020-12-03T10:15:30Z\",\"rank\":21,\"value\":11}";
        String youtubeRank2json = "{\"batch\":1,\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\","
                + "\"market\":\"YouTube\",\"timestamp\":\"2020-12-03T10:15:20Z\",\"rank\":19,\"value\":10}";
        String youtubeDeltajson = String.join(",", asList(
                "{\"interval\":\"6h\",\"change\":2,\"percent\":10.526316}",
                "{\"interval\":\"12h\",\"change\":2,\"percent\":10.526316}",
                "{\"interval\":\"1d\",\"change\":2,\"percent\":10.526316}",
                "{\"interval\":\"3d\",\"change\":2,\"percent\":10.526316}",
                "{\"interval\":\"7d\",\"change\":2,\"percent\":10.526316}",
                "{\"interval\":\"14d\",\"change\":2,\"percent\":10.526316}",
                "{\"interval\":\"30d\",\"change\":2,\"percent\":10.526316}"
        ));
        String youtubeExpected = "{\"market\":\"YouTube\",\"ranks\":[" + youtubeRank1json + "," + youtubeRank2json
                + "],\"deltas\":[" + youtubeDeltajson + "]}";

        String expected = "[" + twitterExpected + "," + youtubeExpected + "]";
        assertEquals(expected, json);

        List<MarketRankCollection> fetched = convert(json, new MarketRankCollectionListTypeRef());
        assertEquals(collections, fetched);
    }
}
