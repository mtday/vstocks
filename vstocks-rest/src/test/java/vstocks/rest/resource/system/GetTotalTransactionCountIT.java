package vstocks.rest.resource.system;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.system.TotalTransactionCountService;
import vstocks.model.ErrorResponse;
import vstocks.model.system.TotalTransactionCount;
import vstocks.model.system.TotalTransactionCountCollection;
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

public class GetTotalTransactionCountIT extends ResourceTest {
    @Test
    public void testNoAuthorizationHeader() {
        Response response = target("/system/transaction-count/total").request().get();

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

        Response response = target("/system/transaction-count/total")
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
    public void testTotalTransactionCountWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        TotalTransactionCount txCount1 = new TotalTransactionCount().setTimestamp(timestamp).setCount(12L);
        TotalTransactionCount txCount2 = new TotalTransactionCount()
                .setTimestamp(timestamp.minusSeconds(10))
                .setCount(13L);
        List<TotalTransactionCount> totalTxCounts = asList(txCount1, txCount2);

        TotalTransactionCountCollection totalTxCountCollection = new TotalTransactionCountCollection()
                .setCounts(totalTxCounts)
                .setDeltas(getDeltas(totalTxCounts, TotalTransactionCount::getTimestamp, TotalTransactionCount::getCount));

        TotalTransactionCountService totalTxCountService = mock(TotalTransactionCountService.class);
        when(totalTxCountService.getLatest()).thenReturn(totalTxCountCollection);
        when(getServiceFactory().getTotalTransactionCountService()).thenReturn(totalTxCountService);

        Response response = target("/system/transaction-count/total")
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

        TotalTransactionCountCollection fetched = convert(json, TotalTransactionCountCollection.class);
        assertEquals(totalTxCountCollection, fetched);
    }
}
