package vstocks.rest.resource.user.portfolio.market;

import org.junit.Test;
import vstocks.db.StockActivityLogService;
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
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.DatabaseField.PRICE;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetAllMarketActivityIT extends ResourceTest {
    @Test
    public void testUserPortfolioAllMarketActivityNoAuthorizationHeader() {
        Response response = target("/user/portfolio/market/activity").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"status\":401,\"message\":\"Missing or invalid JWT authorization bearer token\"}", json);

        ErrorResponse errorResponse = convert(json, ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioAllMarketActivityNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/market/activity")
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
    public void testUserPortfolioAllMarketActivityPageAndSort() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Page page = Page.from(2, 15, 0, 0);
        List<Sort> sort = asList(USER_ID.toSort(), PRICE.toSort(DESC));
        Results<StockActivityLog> results = new Results<StockActivityLog>().setPage(page).setResults(emptyList());
        StockActivityLogService stockActivityLogService = mock(StockActivityLogService.class);
        when(stockActivityLogService.getForUser(eq(getUser().getId()), any(), eq(page), eq(sort))).thenReturn(results);
        when(getServiceFactory().getStockActivityLogService()).thenReturn(stockActivityLogService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/market/activity")
                .queryParam("pageNum", page.getPage())
                .queryParam("pageSize", page.getSize())
                .queryParam("sort", "USER_ID,PRICE:DESC")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"page\":{\"page\":2,\"size\":15,\"totalPages\":0,\"firstRow\":null,\"lastRow\":null,"
                + "\"totalRows\":0},\"results\":[]}", json);

        Results<StockActivityLog> fetched = convert(json, new StockActivityLogResultsTypeRef());
        assertEquals(results, fetched);
    }

    @Test
    public void testUserPortfolioAllMarketActivityWithData() {
        UserService userService = mock(UserService.class);
        when(userService.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getServiceFactory().getUserService()).thenReturn(userService);

        Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z").truncatedTo(SECONDS);
        StockActivityLog stockActivityLog = new StockActivityLog()
                .setId("id")
                .setUserId(getUser().getId())
                .setType(STOCK_BUY)
                .setTimestamp(timestamp)
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("name")
                .setProfileImage("image")
                .setShares(10L)
                .setPrice(20L)
                .setValue(10L * 20L);
        Results<StockActivityLog> results = new Results<StockActivityLog>()
                .setPage(Page.from(1, 20, 1, 1))
                .setResults(singletonList(stockActivityLog));
        StockActivityLogService stockActivityLogService = mock(StockActivityLogService.class);
        when(stockActivityLogService.getForUser(eq(getUser().getId()), any(), any(), any())).thenReturn(results);
        when(getServiceFactory().getStockActivityLogService()).thenReturn(stockActivityLogService);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/market/activity")
                .request()
                .header(AUTHORIZATION, "Bearer token")
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        String json = response.readEntity(String.class);
        assertEquals("{\"page\":{\"page\":1,\"size\":20,\"totalPages\":1,\"firstRow\":1,\"lastRow\":1,"
                + "\"totalRows\":1},\"results\":[{\"id\":\"id\",\"userId\":\"cd2bfcff-e5fe-34a1-949d-101994d0987f\","
                + "\"type\":\"STOCK_BUY\",\"timestamp\":\"2020-12-03T10:15:30Z\",\"market\":\"Twitter\","
                + "\"symbol\":\"symbol\",\"name\":\"name\",\"profileImage\":\"image\",\"shares\":10,\"price\":20,"
                + "\"value\":200}]}", json);

        Results<StockActivityLog> fetched = convert(json, new StockActivityLogResultsTypeRef());
        assertEquals(results, fetched);
    }
}
