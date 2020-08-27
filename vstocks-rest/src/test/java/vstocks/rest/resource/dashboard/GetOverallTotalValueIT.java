package vstocks.rest.resource.dashboard;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.system.OverallTotalValueService;
import vstocks.model.Delta;
import vstocks.model.ErrorResponse;
import vstocks.model.system.OverallTotalValue;
import vstocks.model.system.OverallTotalValueCollection;
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
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetOverallTotalValueIT extends ResourceTest {
    @Test
    public void testOverallTotalValueNoAuthorizationHeader() {
        Response response = target("/dashboard/overall/total").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testOverallTotalValueNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/dashboard/overall/total")
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
    public void testOverallTotalValueWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");
        OverallTotalValue overallTotalValue1 = new OverallTotalValue()
                .setTimestamp(timestamp)
                .setValue(20);
        OverallTotalValue overallTotalValue2 = new OverallTotalValue()
                .setTimestamp(timestamp.minusSeconds(10))
                .setValue(18);

        List<OverallTotalValue> values = asList(overallTotalValue1, overallTotalValue2);
        List<Delta> deltas = getDeltas(values, OverallTotalValue::getTimestamp, OverallTotalValue::getValue);
        OverallTotalValueCollection collection =
                new OverallTotalValueCollection().setValues(values).setDeltas(deltas);

        OverallTotalValueService overallTotalValueService = mock(OverallTotalValueService.class);
        when(overallTotalValueService.getLatest()).thenReturn(collection);
        when(getServiceFactory().getOverallTotalValueService()).thenReturn(overallTotalValueService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/dashboard/overall/total")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String value1json = "{\"timestamp\":\"2020-12-03T10:15:30Z\",\"value\":20}";
        String value2json = "{\"timestamp\":\"2020-12-03T10:15:20Z\",\"value\":18}";
        String deltajson = String.join(",", asList(
                "{\"interval\":\"6h\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"12h\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"1d\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"3d\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"7d\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"14d\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}",
                "{\"interval\":\"30d\",\"oldest\":18,\"newest\":20,\"change\":2,\"percent\":11.111112}"
        ));
        String expected = "{\"values\":[" + value1json + "," + value2json + "],\"deltas\":[" + deltajson + "]}";
        assertEquals(expected, json);

        OverallTotalValueCollection fetched = convert(json, OverallTotalValueCollection.class);
        assertEquals(collection, fetched);
    }
}
