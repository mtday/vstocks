package vstocks.rest.resource.user.portfolio;

import org.junit.Test;
import vstocks.db.PortfolioValueDB;
import vstocks.db.PortfolioValueRankDB;
import vstocks.db.UserDB;
import vstocks.model.ErrorResponse;
import vstocks.model.PortfolioValue;
import vstocks.model.PortfolioValueRank;
import vstocks.rest.ResourceTest;
import vstocks.rest.resource.user.portfolio.GetUserPortfolio.UserPortfolioResponse;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

public class GetUserPortfolioIT extends ResourceTest {
    @Test
    public void testGetEarliest() {
        Instant earliest = GetUserPortfolio.getEarliest(1);
        assertTrue(earliest.toString().endsWith("T00:00:00Z"));
    }

    @Test
    public void testUserPortfolioNoAuthorizationHeader() {
        Response response = target("/user/portfolio").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio") .request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioNoData() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);

        PortfolioValueDB portfolioValueDB = mock(PortfolioValueDB.class);
        when(portfolioValueDB.getForUserSince(eq(getUser().getId()), any(), any())).thenReturn(emptyList());
        when(getDBFactory().getPortfolioValueDB()).thenReturn(portfolioValueDB);

        PortfolioValueRankDB portfolioValueRankDB = mock(PortfolioValueRankDB.class);
        when(portfolioValueRankDB.getForUserSince(eq(getUser().getId()), any(), any())).thenReturn(emptyList());
        when(getDBFactory().getPortfolioValueRankDB()).thenReturn(portfolioValueRankDB);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        UserPortfolioResponse userPortfolioResponse = response.readEntity(UserPortfolioResponse.class);
        assertNull(userPortfolioResponse.getCurrentPortfolioValue());
        assertTrue(userPortfolioResponse.getHistoricalPortfolioValues().isEmpty());
        assertNull(userPortfolioResponse.getCurrentPortfolioValueRank());
        assertTrue(userPortfolioResponse.getHistoricalPortfolioValueRanks().isEmpty());
    }

    @Test
    public void testUserPortfolioWithData() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);

        List<PortfolioValue> portfolioValues = Stream.iterate(1, i -> i + 1).limit(10)
                .map(i -> new PortfolioValue()
                            .setUserId(getUser().getId())
                            .setCredits(i * 10)
                            .setMarketValues(Map.of(TWITTER, i * 10L, YOUTUBE, i * 10L))
                            .setTotal(i * 30)
                            .setTimestamp(Instant.now().minusSeconds(i * 10).truncatedTo(SECONDS)))
                .collect(toList());

        PortfolioValueDB portfolioValueDB = mock(PortfolioValueDB.class);
        when(portfolioValueDB.getForUserSince(eq(getUser().getId()), any(), any())).thenReturn(portfolioValues);
        when(getDBFactory().getPortfolioValueDB()).thenReturn(portfolioValueDB);

        List<PortfolioValueRank> portfolioValueRanks = Stream.iterate(1, i -> i + 1).limit(10)
                .map(i -> new PortfolioValueRank()
                        .setUserId(getUser().getId())
                        .setRank(i)
                        .setTimestamp(Instant.now().minusSeconds(i * 10).truncatedTo(SECONDS)))
                .collect(toList());

        PortfolioValueRankDB portfolioValueRankDB = mock(PortfolioValueRankDB.class);
        when(portfolioValueRankDB.getForUserSince(eq(getUser().getId()), any(), any())).thenReturn(portfolioValueRanks);
        when(getDBFactory().getPortfolioValueRankDB()).thenReturn(portfolioValueRankDB);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        UserPortfolioResponse userPortfolioResponse = response.readEntity(UserPortfolioResponse.class);

        // make sure the current value is correct
        PortfolioValue expectedCurrentValue = portfolioValues.iterator().next();
        assertEquals(expectedCurrentValue.getUserId(), userPortfolioResponse.getCurrentPortfolioValue().getUserId());
        assertEquals(expectedCurrentValue.getCredits(), userPortfolioResponse.getCurrentPortfolioValue().getCredits());
        assertEquals(expectedCurrentValue.getMarketValues(), userPortfolioResponse.getCurrentPortfolioValue().getMarketValues());
        assertEquals(expectedCurrentValue.getTotal(), userPortfolioResponse.getCurrentPortfolioValue().getTotal());
        assertEquals(expectedCurrentValue.getTimestamp(), userPortfolioResponse.getCurrentPortfolioValue().getTimestamp());

        // make sure the historical values are correct
        assertEquals(portfolioValues.size(), userPortfolioResponse.getHistoricalPortfolioValues().size());
        Iterator<PortfolioValue> expectedValueIter = portfolioValues.iterator();
        Iterator<PortfolioValue> actualValueIter = userPortfolioResponse.getHistoricalPortfolioValues().iterator();
        while (expectedValueIter.hasNext() && actualValueIter.hasNext()) {
            PortfolioValue expected = expectedValueIter.next();
            PortfolioValue actual = actualValueIter.next();
            assertEquals(expected.getUserId(), actual.getUserId());
            assertEquals(expected.getCredits(), actual.getCredits());
            assertEquals(expected.getMarketValues(), actual.getMarketValues());
            assertEquals(expected.getTotal(), actual.getTotal());
            assertEquals(expected.getTimestamp(), actual.getTimestamp());
        }

        // make sure the current value rank is correct
        PortfolioValueRank expectedCurrentRank = portfolioValueRanks.iterator().next();
        assertEquals(expectedCurrentRank.getUserId(), userPortfolioResponse.getCurrentPortfolioValueRank().getUserId());
        assertEquals(expectedCurrentRank.getRank(), userPortfolioResponse.getCurrentPortfolioValueRank().getRank());
        assertEquals(expectedCurrentRank.getTimestamp(), userPortfolioResponse.getCurrentPortfolioValueRank().getTimestamp());

        // make sure the historical values are correct
        assertEquals(portfolioValueRanks.size(), userPortfolioResponse.getHistoricalPortfolioValueRanks().size());
        Iterator<PortfolioValueRank> expectedRankIter = portfolioValueRanks.iterator();
        Iterator<PortfolioValueRank> actualRankIter = userPortfolioResponse.getHistoricalPortfolioValueRanks().iterator();
        while (expectedRankIter.hasNext() && actualRankIter.hasNext()) {
            PortfolioValueRank expected = expectedRankIter.next();
            PortfolioValueRank actual = actualRankIter.next();
            assertEquals(expected.getUserId(), actual.getUserId());
            assertEquals(expected.getRank(), actual.getRank());
            assertEquals(expected.getTimestamp(), actual.getTimestamp());
        }
    }
}
