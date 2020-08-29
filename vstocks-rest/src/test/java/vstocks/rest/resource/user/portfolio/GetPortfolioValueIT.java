package vstocks.rest.resource.user.portfolio;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.portfolio.PortfolioValueService;
import vstocks.model.Delta;
import vstocks.model.ErrorResponse;
import vstocks.model.Market;
import vstocks.model.portfolio.*;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Delta.getDeltas;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.User.generateId;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetPortfolioValueIT extends ResourceTest {
    @Test
    public void testUserPortfolioValueNoAuthorizationHeader() {
        Response response = target("/user/portfolio/value").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioValueNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/value").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioValueNotFound() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        PortfolioValueService portfolioValueService = mock(PortfolioValueService.class);
        when(portfolioValueService.getForUser(eq(getUser().getId()))).thenReturn(empty());
        when(getServiceFactory().getPortfolioValueService()).thenReturn(portfolioValueService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/value").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(NOT_FOUND.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":404,\"message\":\"Failed to find portfolio value for user\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(NOT_FOUND.getStatusCode(), errorResponse.getStatus());
        assertEquals("Failed to find portfolio value for user", errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioValueWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        List<MarketValue> marketValues = Arrays.stream(Market.values())
                .map(market -> new MarketValue().setMarket(market).setValue(market.ordinal()))
                .collect(toList());
        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary()
                .setUserId(generateId("user@domain.com"))
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        CreditRank creditRank1 = new CreditRank()
                .setBatch(2)
                .setUserId(getUser().getId())
                .setTimestamp(timestamp)
                .setRank(20)
                .setValue(10);
        CreditRank creditRank2 = new CreditRank()
                .setBatch(1)
                .setUserId(getUser().getId())
                .setTimestamp(timestamp.minusSeconds(10))
                .setRank(18)
                .setValue(9);
        List<CreditRank> creditRanks = asList(creditRank1, creditRank2);
        List<Delta> creditDeltas = getDeltas(creditRanks, CreditRank::getTimestamp, CreditRank::getRank);
        CreditRankCollection creditRankCollection =
                new CreditRankCollection().setRanks(creditRanks).setDeltas(creditDeltas);

        MarketRank marketRank1 = new MarketRank()
                .setBatch(2)
                .setUserId(getUser().getId())
                .setMarket(TWITTER)
                .setTimestamp(timestamp)
                .setRank(20)
                .setValue(10);
        MarketRank marketRank2 = new MarketRank()
                .setBatch(1)
                .setUserId(getUser().getId())
                .setMarket(TWITTER)
                .setTimestamp(timestamp.minusSeconds(10))
                .setRank(18)
                .setValue(9);
        List<MarketRank> marketRanks = asList(marketRank1, marketRank2);
        List<Delta> marketDeltas = getDeltas(marketRanks, MarketRank::getTimestamp, MarketRank::getRank);
        MarketRankCollection marketRankCollection =
                new MarketRankCollection().setMarket(TWITTER).setRanks(marketRanks).setDeltas(marketDeltas);
        List<MarketRankCollection> marketRankCollections = singletonList(marketRankCollection);

        MarketTotalRank marketTotalRank1 = new MarketTotalRank()
                .setBatch(2)
                .setUserId(getUser().getId())
                .setTimestamp(timestamp)
                .setRank(20)
                .setValue(10);
        MarketTotalRank marketTotalRank2 = new MarketTotalRank()
                .setBatch(1)
                .setUserId(getUser().getId())
                .setTimestamp(timestamp.minusSeconds(10))
                .setRank(18)
                .setValue(9);
        List<MarketTotalRank> marketTotalRanks = asList(marketTotalRank1, marketTotalRank2);
        List<Delta> marketTotalDeltas =
                getDeltas(marketTotalRanks, MarketTotalRank::getTimestamp, MarketTotalRank::getRank);
        MarketTotalRankCollection marketTotalRankCollection =
                new MarketTotalRankCollection().setRanks(marketTotalRanks).setDeltas(marketTotalDeltas);

        TotalRank totalRank1 = new TotalRank()
                .setBatch(2)
                .setUserId(getUser().getId())
                .setTimestamp(timestamp)
                .setRank(20)
                .setValue(10);
        TotalRank totalRank2 = new TotalRank()
                .setBatch(1)
                .setUserId(getUser().getId())
                .setTimestamp(timestamp.minusSeconds(10))
                .setRank(18)
                .setValue(9);
        List<TotalRank> totalRanks = asList(totalRank1, totalRank2);
        List<Delta> totalDeltas = getDeltas(totalRanks, TotalRank::getTimestamp, TotalRank::getRank);
        TotalRankCollection totalRankCollection = new TotalRankCollection().setRanks(totalRanks).setDeltas(totalDeltas);

        PortfolioValue portfolioValue = new PortfolioValue()
                .setSummary(portfolioValueSummary)
                .setCreditRanks(creditRankCollection)
                .setMarketRanks(marketRankCollections)
                .setMarketTotalRanks(marketTotalRankCollection)
                .setTotalRanks(totalRankCollection);

        PortfolioValueService portfolioValueService = mock(PortfolioValueService.class);
        when(portfolioValueService.getForUser(eq(getUser().getId()))).thenReturn(Optional.of(portfolioValue));
        when(getServiceFactory().getPortfolioValueService()).thenReturn(portfolioValueService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/value").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertNotEquals("", json); // skipping real json comparison

        PortfolioValue fetched = convert(json, PortfolioValue.class);
        assertEquals(portfolioValue, fetched);
    }
}
