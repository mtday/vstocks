package vstocks.rest.resource.user.portfolio;

import org.junit.Test;
import vstocks.db.PortfolioValueDB;
import vstocks.db.UserDB;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.ErrorResponse;
import vstocks.model.PortfolioValue;
import vstocks.model.rest.UserPortfolioValueResponse;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetValuesIT extends ResourceTest {
    @Test
    public void testUserPortfolioValuesNoAuthorizationHeader() {
        Response response = target("/user/portfolio/values").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioValuesNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/values").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioValuesNoData() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);

        PortfolioValueDB portfolioValueDB = mock(PortfolioValueDB.class);
        when(portfolioValueDB.getForUserSince(eq(getUser().getId()), any(), any())).thenReturn(emptyList());
        when(getDBFactory().getPortfolioValueDB()).thenReturn(portfolioValueDB);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/values").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        UserPortfolioValueResponse userPortfolioValueResponse = response.readEntity(UserPortfolioValueResponse.class);
        assertNull(userPortfolioValueResponse.getCurrentValue());
        assertTrue(userPortfolioValueResponse.getHistoricalValues().isEmpty());
        assertEquals(DeltaInterval.values().length, userPortfolioValueResponse.getDeltas().size());
    }

    @Test
    public void testUserPortfolioWithData() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);

        List<PortfolioValue> values = Stream.iterate(1, i -> i + 1).limit(10)
                .map(i -> new PortfolioValue()
                            .setUserId(getUser().getId())
                            .setCredits(i * 10)
                            .setMarketValues(Map.of(TWITTER, i * 10L, YOUTUBE, i * 10L))
                            .setTotal(i * 30)
                            .setTimestamp(Instant.now().minusSeconds(i * 10).truncatedTo(SECONDS)))
                .collect(toList());

        PortfolioValueDB portfolioValueDB = mock(PortfolioValueDB.class);
        when(portfolioValueDB.getForUserSince(eq(getUser().getId()), any(), any())).thenReturn(values);
        when(getDBFactory().getPortfolioValueDB()).thenReturn(portfolioValueDB);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/values").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        UserPortfolioValueResponse userPortfolioValueResponse = response.readEntity(UserPortfolioValueResponse.class);

        // make sure the current value is correct
        PortfolioValue expectedCurrentValue = values.iterator().next();
        assertEquals(expectedCurrentValue.getUserId(), userPortfolioValueResponse.getCurrentValue().getUserId());
        assertEquals(expectedCurrentValue.getCredits(), userPortfolioValueResponse.getCurrentValue().getCredits());
        assertEquals(expectedCurrentValue.getMarketValues(), userPortfolioValueResponse.getCurrentValue().getMarketValues());
        assertEquals(expectedCurrentValue.getTotal(), userPortfolioValueResponse.getCurrentValue().getTotal());
        assertEquals(expectedCurrentValue.getTimestamp(), userPortfolioValueResponse.getCurrentValue().getTimestamp());

        // make sure the historical values are correct
        assertEquals(values.size(), userPortfolioValueResponse.getHistoricalValues().size());
        Iterator<PortfolioValue> expectedValueIter = values.iterator();
        Iterator<PortfolioValue> actualValueIter = userPortfolioValueResponse.getHistoricalValues().iterator();
        while (expectedValueIter.hasNext() && actualValueIter.hasNext()) {
            PortfolioValue expected = expectedValueIter.next();
            PortfolioValue actual = actualValueIter.next();
            assertEquals(expected.getUserId(), actual.getUserId());
            assertEquals(expected.getCredits(), actual.getCredits());
            assertEquals(expected.getMarketValues(), actual.getMarketValues());
            assertEquals(expected.getTotal(), actual.getTotal());
            assertEquals(expected.getTimestamp(), actual.getTimestamp());
        }

        assertEquals(DeltaInterval.values().length, userPortfolioValueResponse.getDeltas().size());
        Arrays.stream(DeltaInterval.values()).forEach(interval -> {
            Delta delta = new Delta().setInterval(interval).setChange(-270).setPercent(-90f);
            assertEquals(delta, userPortfolioValueResponse.getDeltas().get(interval));
        });
    }
}
