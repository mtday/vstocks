package vstocks.rest.resource.system;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.system.TotalUserCountService;
import vstocks.model.Delta;
import vstocks.model.ErrorResponse;
import vstocks.model.system.TotalUserCount;
import vstocks.model.system.TotalUserCountCollection;
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
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetTotalUserCountIT extends ResourceTest {
    @Test
    public void testNoAuthorizationHeader() {
        Response response = target("/system/user-count/total").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/system/user-count/total").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testTotalUserCountWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        TotalUserCount userCount1 = new TotalUserCount().setTimestamp(timestamp).setCount(1234L);
        TotalUserCount userCount2 = new TotalUserCount().setTimestamp(timestamp.minusSeconds(10)).setCount(1230L);
        List<TotalUserCount> totalUserCounts = asList(userCount1, userCount2);

        TotalUserCountCollection totalUserCountCollection = new TotalUserCountCollection()
                .setCounts(totalUserCounts)
                .setDeltas(Delta.getDeltas(totalUserCounts, TotalUserCount::getTimestamp, TotalUserCount::getCount));

        TotalUserCountService totalUserCountService = mock(TotalUserCountService.class);
        when(totalUserCountService.getLatest()).thenReturn(totalUserCountCollection);
        when(getServiceFactory().getTotalUserCountService()).thenReturn(totalUserCountService);

        Response response = target("/system/user-count/total").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String value1json = "{\"timestamp\":\"2020-12-03T10:15:30Z\",\"count\":1234}";
        String value2json = "{\"timestamp\":\"2020-12-03T10:15:20Z\",\"count\":1230}";
        String deltajson = String.join(",", asList(
                "{\"interval\":\"6h\",\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"12h\",\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"1d\",\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"3d\",\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"7d\",\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"14d\",\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"30d\",\"change\":4,\"percent\":0.32520324}"
        ));
        String expected = "{\"counts\":[" + value1json + "," + value2json + "],\"deltas\":[" + deltajson + "]}";
        assertEquals(expected, json);

        TotalUserCountCollection fetched = convert(json, TotalUserCountCollection.class);
        assertEquals(totalUserCountCollection, fetched);
    }
}
