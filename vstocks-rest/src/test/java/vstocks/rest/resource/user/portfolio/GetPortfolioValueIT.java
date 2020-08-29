package vstocks.rest.resource.user.portfolio;

import org.junit.Test;
import vstocks.db.UserService;
import vstocks.db.portfolio.PortfolioValueService;
import vstocks.model.ErrorResponse;
import vstocks.model.Market;
import vstocks.model.portfolio.PortfolioValue;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, (long) market.ordinal()));
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(generateId("user@domain.com"))
                .setCredits(1)
                .setMarketTotal(2)
                .setMarketValues(marketValues)
                .setTotal(3);

        PortfolioValueService portfolioValueService = mock(PortfolioValueService.class);
        when(portfolioValueService.getForUser(eq(getUser().getId()))).thenReturn(Optional.of(portfolioValue));
        when(getServiceFactory().getPortfolioValueService()).thenReturn(portfolioValueService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/value").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"credits\":1,\"marketTotal\":2,"
                + "\"marketValues\":{\"Twitter\":0,\"YouTube\":1,\"Instagram\":2,\"Twitch\":3,"
                + "\"Facebook\":4},\"total\":3}", json);

        PortfolioValue fetched = convert(json, PortfolioValue.class);
        assertEquals(portfolioValue, fetched);
    }
}
