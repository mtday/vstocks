package vstocks.rest.resource.system;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.system.ActiveUserCountService;
import vstocks.model.Delta;
import vstocks.model.ErrorResponse;
import vstocks.model.system.ActiveUserCount;
import vstocks.model.system.ActiveUserCountCollection;
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

public class GetActiveUserCountIT extends ResourceTest {
    @Test
    public void testNoAuthorizationHeader() {
        Response response = target("/system/user-count/active").request().get();

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

        Response response = target("/system/user-count/active").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testActiveUserCountWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        ActiveUserCount userCount1 = new ActiveUserCount().setTimestamp(timestamp).setCount(1234L);
        ActiveUserCount userCount2 = new ActiveUserCount().setTimestamp(timestamp.minusSeconds(10)).setCount(1230L);
        List<ActiveUserCount> activeUserCounts = asList(userCount1, userCount2);

        ActiveUserCountCollection activeUserCountCollection = new ActiveUserCountCollection()
                .setCounts(activeUserCounts)
                .setDeltas(Delta.getDeltas(activeUserCounts, ActiveUserCount::getTimestamp, ActiveUserCount::getCount));

        ActiveUserCountService activeUserCountService = mock(ActiveUserCountService.class);
        when(activeUserCountService.getLatest()).thenReturn(activeUserCountCollection);
        when(getServiceFactory().getActiveUserCountService()).thenReturn(activeUserCountService);

        Response response = target("/system/user-count/active").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String value1json = "{\"timestamp\":\"2020-12-03T10:15:30Z\",\"count\":1234}";
        String value2json = "{\"timestamp\":\"2020-12-03T10:15:20Z\",\"count\":1230}";
        String deltajson = String.join(",", asList(
                "{\"interval\":\"6h\",\"oldest\":1230,\"newest\":1234,\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"12h\",\"oldest\":1230,\"newest\":1234,\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"1d\",\"oldest\":1230,\"newest\":1234,\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"3d\",\"oldest\":1230,\"newest\":1234,\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"7d\",\"oldest\":1230,\"newest\":1234,\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"14d\",\"oldest\":1230,\"newest\":1234,\"change\":4,\"percent\":0.32520324}",
                "{\"interval\":\"30d\",\"oldest\":1230,\"newest\":1234,\"change\":4,\"percent\":0.32520324}"
        ));
        String expected = "{\"counts\":[" + value1json + "," + value2json + "],\"deltas\":[" + deltajson + "]}";
        assertEquals(expected, json);

        ActiveUserCountCollection fetched = convert(json, ActiveUserCountCollection.class);
        assertEquals(activeUserCountCollection, fetched);
    }
}
