package vstocks.rest.resource.dashboard;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.system.OverallMarketTotalValueService;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.ErrorResponse;
import vstocks.model.system.OverallMarketTotalValue;
import vstocks.model.system.OverallMarketTotalValueCollection;
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

public class GetOverallMarketTotalValueIT extends ResourceTest {
    @Test
    public void testOverallMarketTotalValueNoAuthorizationHeader() {
        Response response = target("/dashboard/overall/market-total").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testOverallMarketTotalValueNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/dashboard/overall/market-total")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testOverallMarketTotalValueWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");
        OverallMarketTotalValue overallMarketTotalValue1 = new OverallMarketTotalValue()
                .setTimestamp(timestamp)
                .setValue(20);
        OverallMarketTotalValue overallMarketTotalValue2 = new OverallMarketTotalValue()
                .setTimestamp(timestamp.minusSeconds(10))
                .setValue(18);

        List<OverallMarketTotalValue> values = asList(overallMarketTotalValue1, overallMarketTotalValue2);
        Map<DeltaInterval, Delta> deltas =
                Delta.getDeltas(values, OverallMarketTotalValue::getTimestamp, OverallMarketTotalValue::getValue);
        OverallMarketTotalValueCollection collection =
                new OverallMarketTotalValueCollection().setValues(values).setDeltas(deltas);

        OverallMarketTotalValueService overallMarketTotalValueService = mock(OverallMarketTotalValueService.class);
        when(overallMarketTotalValueService.getLatest()).thenReturn(collection);
        when(getServiceFactory().getOverallMarketTotalValueService()).thenReturn(overallMarketTotalValueService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/dashboard/overall/market-total")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(collection, response.readEntity(OverallMarketTotalValueCollection.class));
    }
}
