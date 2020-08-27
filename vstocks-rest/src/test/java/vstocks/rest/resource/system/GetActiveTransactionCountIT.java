package vstocks.rest.resource.system;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.system.ActiveTransactionCountService;
import vstocks.model.ErrorResponse;
import vstocks.model.system.ActiveTransactionCount;
import vstocks.model.system.ActiveTransactionCountCollection;
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

public class GetActiveTransactionCountIT extends ResourceTest {
    @Test
    public void testNoAuthorizationHeader() {
        Response response = target("/system/transaction-count/active").request().get();

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

        Response response = target("/system/transaction-count/active")
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
    public void testActiveTransactionCountWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        ActiveTransactionCount txCount1 = new ActiveTransactionCount().setTimestamp(timestamp).setCount(12L);
        ActiveTransactionCount txCount2 = new ActiveTransactionCount()
                .setTimestamp(timestamp.minusSeconds(10))
                .setCount(13L);
        List<ActiveTransactionCount> activeTxCounts = asList(txCount1, txCount2);

        ActiveTransactionCountCollection activeTxCountCollection = new ActiveTransactionCountCollection()
                .setCounts(activeTxCounts)
                .setDeltas(getDeltas(activeTxCounts, ActiveTransactionCount::getTimestamp, ActiveTransactionCount::getCount));

        ActiveTransactionCountService activeTxCountService = mock(ActiveTransactionCountService.class);
        when(activeTxCountService.getLatest()).thenReturn(activeTxCountCollection);
        when(getServiceFactory().getActiveTransactionCountService()).thenReturn(activeTxCountService);

        Response response = target("/system/transaction-count/active")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        String value1json = "{\"timestamp\":\"2020-12-03T10:15:30Z\",\"count\":12}";
        String value2json = "{\"timestamp\":\"2020-12-03T10:15:20Z\",\"count\":13}";
        String deltajson = String.join(",", asList(
                "{\"interval\":\"6h\",\"change\":-1,\"percent\":-7.692308}",
                "{\"interval\":\"12h\",\"change\":-1,\"percent\":-7.692308}",
                "{\"interval\":\"1d\",\"change\":-1,\"percent\":-7.692308}",
                "{\"interval\":\"3d\",\"change\":-1,\"percent\":-7.692308}",
                "{\"interval\":\"7d\",\"change\":-1,\"percent\":-7.692308}",
                "{\"interval\":\"14d\",\"change\":-1,\"percent\":-7.692308}",
                "{\"interval\":\"30d\",\"change\":-1,\"percent\":-7.692308}"
        ));
        String expected = "{\"counts\":[" + value1json + "," + value2json + "],\"deltas\":[" + deltajson + "]}";
        assertEquals(expected, json);

        ActiveTransactionCountCollection fetched = convert(json, ActiveTransactionCountCollection.class);
        assertEquals(activeTxCountCollection, fetched);
    }
}
