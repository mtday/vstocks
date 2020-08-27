package vstocks.rest.resource.dashboard;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.system.OverallMarketValueService;
import vstocks.model.Delta;
import vstocks.model.ErrorResponse;
import vstocks.model.system.OverallMarketValue;
import vstocks.model.system.OverallMarketValueCollection;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
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
import static vstocks.model.Delta.getDeltas;
import static vstocks.model.Market.TWITTER;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetOverallMarketValueIT extends ResourceTest {
    @Test
    public void testOverallMarketValueNoAuthorizationHeader() {
        Response response = target("/dashboard/overall/market/TWITTER").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testOverallMarketValueNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/dashboard/overall/market/TWITTER")
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
    public void testOverallMarketValueWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");
        OverallMarketValue overallMarketValue1 = new OverallMarketValue()
                .setMarket(TWITTER)
                .setTimestamp(timestamp)
                .setValue(20);
        OverallMarketValue overallMarketValue2 = new OverallMarketValue()
                .setMarket(TWITTER)
                .setTimestamp(timestamp.minusSeconds(10))
                .setValue(18);

        List<OverallMarketValue> values = asList(overallMarketValue1, overallMarketValue2);
        List<Delta> deltas = getDeltas(values, OverallMarketValue::getTimestamp, OverallMarketValue::getValue);
        OverallMarketValueCollection collection =
                new OverallMarketValueCollection().setMarket(TWITTER).setValues(values).setDeltas(deltas);

        OverallMarketValueService overallMarketValueService = mock(OverallMarketValueService.class);
        when(overallMarketValueService.getLatest(eq(TWITTER))).thenReturn(collection);
        when(getServiceFactory().getOverallMarketValueService()).thenReturn(overallMarketValueService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/dashboard/overall/market/TWITTER")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String value1json = "{\"market\":\"Twitter\",\"timestamp\":\"2020-12-03T10:15:30Z\",\"value\":20}";
        String value2json = "{\"market\":\"Twitter\",\"timestamp\":\"2020-12-03T10:15:20Z\",\"value\":18}";
        String deltajson = String.join(",", asList(
                "{\"interval\":\"6h\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"12h\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"1d\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"3d\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"7d\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"14d\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"30d\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}"
        ));
        String expected = "{\"market\":\"Twitter\",\"values\":[" + value1json + "," + value2json
                + "],\"deltas\":[" + deltajson + "]}";
        assertEquals(expected, json);

        OverallMarketValueCollection fetched = convert(json, OverallMarketValueCollection.class);
        assertEquals(collection, fetched);
    }
}
