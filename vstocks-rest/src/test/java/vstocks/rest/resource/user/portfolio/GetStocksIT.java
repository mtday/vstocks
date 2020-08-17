package vstocks.rest.resource.user.portfolio;

import org.junit.Test;
import vstocks.db.PricedUserStockDB;
import vstocks.db.UserDB;
import vstocks.model.*;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.*;

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
import static vstocks.model.DeltaInterval.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetStocksIT extends ResourceTest {
    @Test
    public void testUserPortfolioStocksNoAuthorizationHeader() {
        Response response = target("/user/portfolio/stocks").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioStocksNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/stocks").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioStocksPageAndSort() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);

        Page page = new Page().setPage(2).setSize(15);
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(), PRICE.toSort(DESC)));
        Results<PricedUserStock> results =
                new Results<PricedUserStock>().setTotal(0).setPage(page).setResults(emptyList());
        PricedUserStockDB pricedUserStockDB = mock(PricedUserStockDB.class);
        when(pricedUserStockDB.getForUser(eq(getUser().getId()), eq(page), eq(sort))).thenReturn(results);
        when(getDBFactory().getPricedUserStockDB()).thenReturn(pricedUserStockDB);

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

        assertEquals(results, response.readEntity(new PricedUserStockResultsGenericType()));
    }

    @Test
    public void testUserPortfolioStocksWithData() {
        UserDB userDB = mock(UserDB.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserDB()).thenReturn(userDB);

        PricedUserStock pricedUserStock = new PricedUserStock()
                .setUserId(getUser().getId())
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setShares(10)
                .setPrice(20)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        Results<PricedUserStock> results = new Results<PricedUserStock>()
                .setTotal(1)
                .setPage(new Page())
                .setResults(singletonList(pricedUserStock));
        PricedUserStockDB pricedUserStockDB = mock(PricedUserStockDB.class);
        when(pricedUserStockDB.getForUser(eq(getUser().getId()), any(), any())).thenReturn(results);
        when(getDBFactory().getPricedUserStockDB()).thenReturn(pricedUserStockDB);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/stocks").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(results, response.readEntity(new PricedUserStockResultsGenericType()));
    }
}
