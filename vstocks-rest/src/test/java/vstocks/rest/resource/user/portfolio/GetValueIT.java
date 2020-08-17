package vstocks.rest.resource.user.portfolio;

import org.junit.Test;
import vstocks.db.PortfolioValueDB;
import vstocks.db.UserDB;
import vstocks.model.Delta;
import vstocks.model.ErrorResponse;
import vstocks.model.PortfolioValue;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.DeltaInterval.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetValueIT extends ResourceTest {
    @Test
    public void testUserPortfolioValuesNoAuthorizationHeader() {
        Response response = target("/user/portfolio/value").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioValuesNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/value").request().header(AUTHORIZATION, "Bearer token").get();

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
        when(portfolioValueDB.getLatest(eq(getUser().getId()))).thenReturn(empty());
        when(getDBFactory().getPortfolioValueDB()).thenReturn(portfolioValueDB);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/value").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), errorResponse.getStatus());
        assertEquals("No portfolio value found", errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioValuesWithData() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);

        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(getUser().getId())
                .setTimestamp(Instant.now().minusSeconds(10).truncatedTo(SECONDS))
                .setCredits(10_000)
                .setMarketValues(Map.of(TWITTER, 1_000L, YOUTUBE, 2_000L))
                .setTotal(13_000)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));

        PortfolioValueDB portfolioValueDB = mock(PortfolioValueDB.class);
        when(portfolioValueDB.getLatest(eq(getUser().getId()))).thenReturn(Optional.of(portfolioValue));
        when(getDBFactory().getPortfolioValueDB()).thenReturn(portfolioValueDB);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/value").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        PortfolioValue value = response.readEntity(PortfolioValue.class);

        assertEquals(portfolioValue.getUserId(), value.getUserId());
        assertEquals(portfolioValue.getCredits(), value.getCredits());
        assertEquals(portfolioValue.getMarketValues(), value.getMarketValues());
        assertEquals(portfolioValue.getTotal(), value.getTotal());
        assertEquals(portfolioValue.getTimestamp(), value.getTimestamp());
        assertEquals(portfolioValue.getDeltas(), value.getDeltas());
    }
}
