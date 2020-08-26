package vstocks.rest.resource.dashboard;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.system.OverallCreditValueService;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.ErrorResponse;
import vstocks.model.system.OverallCreditValue;
import vstocks.model.system.OverallCreditValueCollection;
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

public class GetOverallCreditValueIT extends ResourceTest {
    @Test
    public void testOverallCreditValueNoAuthorizationHeader() {
        Response response = target("/dashboard/overall/credits").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testOverallCreditValueNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/dashboard/overall/credits")
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
    public void testOverallCreditValueWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");
        OverallCreditValue overallCreditValue1 = new OverallCreditValue()
                .setTimestamp(timestamp)
                .setValue(20);
        OverallCreditValue overallCreditValue2 = new OverallCreditValue()
                .setTimestamp(timestamp.minusSeconds(10))
                .setValue(18);

        List<OverallCreditValue> values = asList(overallCreditValue1, overallCreditValue2);
        Map<DeltaInterval, Delta> deltas =
                Delta.getDeltas(values, OverallCreditValue::getTimestamp, OverallCreditValue::getValue);
        OverallCreditValueCollection collection =
                new OverallCreditValueCollection().setValues(values).setDeltas(deltas);

        OverallCreditValueService overallCreditValueService = mock(OverallCreditValueService.class);
        when(overallCreditValueService.getLatest()).thenReturn(collection);
        when(getServiceFactory().getOverallCreditValueService()).thenReturn(overallCreditValueService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/dashboard/overall/credits")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String value1json = "{\"timestamp\":\"2020-12-03T10:15:30Z\",\"value\":20}";
        String value2json = "{\"timestamp\":\"2020-12-03T10:15:20Z\",\"value\":18}";
        String deltajson = String.join(",", asList(
                "\"6h\":{\"interval\":\"6h\",\"change\":2,\"percent\":11.111112}",
                "\"12h\":{\"interval\":\"12h\",\"change\":2,\"percent\":11.111112}",
                "\"1d\":{\"interval\":\"1d\",\"change\":2,\"percent\":11.111112}",
                "\"3d\":{\"interval\":\"3d\",\"change\":2,\"percent\":11.111112}",
                "\"7d\":{\"interval\":\"7d\",\"change\":2,\"percent\":11.111112}",
                "\"14d\":{\"interval\":\"14d\",\"change\":2,\"percent\":11.111112}",
                "\"30d\":{\"interval\":\"30d\",\"change\":2,\"percent\":11.111112}"
        ));
        String expected = "{\"values\":[" + value1json + "," + value2json + "],\"deltas\":{" + deltajson + "}}";
        assertEquals(expected, json);

        OverallCreditValueCollection fetched = convert(json, OverallCreditValueCollection.class);
        assertEquals(collection, fetched);
    }
}
