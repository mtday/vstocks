package vstocks.rest.resource.user.portfolio;

import org.junit.Test;
import vstocks.db.PricedUserStockService;
import vstocks.db.UserService;
import vstocks.model.*;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Optional.empty;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.DatabaseField.PRICE;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetStocksIT extends ResourceTest {
    @Test
    public void testUserPortfolioStocksNoAuthorizationHeader() {
        Response response = target("/user/portfolio/stocks").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioStocksNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/stocks").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioStocksPageAndSort() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Page page = new Page().setPage(2).setSize(15);
        List<Sort> sort = asList(USER_ID.toSort(), PRICE.toSort(DESC));
        Results<PricedUserStock> results =
                new Results<PricedUserStock>().setTotal(0).setPage(page).setResults(emptyList());
        PricedUserStockService pricedUserStockService = mock(PricedUserStockService.class);
        when(pricedUserStockService.getForUser(eq(getUser().getId()), eq(page), eq(sort))).thenReturn(results);
        when(getServiceFactory().getPricedUserStockService()).thenReturn(pricedUserStockService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/stocks")
                .queryParam("pageNum", page.getPage())
                .queryParam("pageSize", page.getSize())
                .queryParam("sort", "USER_ID,PRICE:DESC")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"page\":{\"page\":2,\"size\":15},\"total\":0,\"results\":[]}", json);

        Results<PricedUserStock> fetched = convert(json, new PricedUserStockResultsTypeRef());
        assertEquals(results, fetched);
    }

    @Test
    public void testUserPortfolioStocksWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(getUser().getId())
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(timestamp)
                .setShares(10)
                .setPrice(20);
        Results<PricedUserStock> results = new Results<PricedUserStock>()
                .setTotal(1)
                .setPage(new Page())
                .setResults(singletonList(pricedUserStock));
        PricedUserStockService pricedUserStockService = mock(PricedUserStockService.class);
        when(pricedUserStockService.getForUser(eq(getUser().getId()), any(), any())).thenReturn(results);
        when(getServiceFactory().getPricedUserStockService()).thenReturn(pricedUserStockService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/stocks").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"page\":{\"page\":1,\"size\":25},\"total\":1,\"results\":["
                + "{\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\",\"market\":\"Twitter\",\"symbol\":\"symbol\","
                + "\"timestamp\":\"2020-12-03T10:15:30Z\",\"shares\":10,\"price\":20}]}", json);

        Results<PricedUserStock> fetched = convert(json, new PricedUserStockResultsTypeRef());
        assertEquals(results, fetched);
    }
}
