package vstocks.rest.resource.user.portfolio;

import org.junit.Test;
import vstocks.db.PortfolioValueRankDB;
import vstocks.db.UserService;
import vstocks.model.Delta;
import vstocks.model.ErrorResponse;
import vstocks.model.PortfolioValueRank;
import vstocks.model.PortfolioValueRankCollection;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
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
import static vstocks.rest.security.JwtTokenFilter.INVALID_JWT_MESSAGE;

public class GetRankIT extends ResourceTest {
    @Test
    public void testUserPortfolioRanksNoAuthorizationHeader() {
        Response response = target("/user/portfolio/rank").request().get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioRanksNoValidToken() {
        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(empty());

        Response response = target("/user/portfolio/rank").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(UNAUTHORIZED.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
        assertEquals(UNAUTHORIZED.getStatusCode(), errorResponse.getStatus());
        assertEquals(INVALID_JWT_MESSAGE, errorResponse.getMessage());
    }

    @Test
    public void testUserPortfolioRanksWithData() {
        UserService userDB = mock(UserService.class);
        when(userDB.get(eq(getUser().getId()))).thenReturn(Optional.of(getUser()));
        when(getDBFactory().getUserService()).thenReturn(userDB);

        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank()
                .setUserId(getUser().getId())
                .setRank(10)
                .setTimestamp(Instant.now().minusSeconds(10).truncatedTo(SECONDS));
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank()
                .setUserId(getUser().getId())
                .setRank(10)
                .setTimestamp(Instant.now().minusSeconds(10).truncatedTo(SECONDS));

        PortfolioValueRankCollection collection = new PortfolioValueRankCollection()
                .setRanks(asList(portfolioValueRank1, portfolioValueRank2))
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));

        PortfolioValueRankDB portfolioValueRankDB = mock(PortfolioValueRankDB.class);
        when(portfolioValueRankDB.getLatest(eq(getUser().getId()))).thenReturn(collection);
        when(getDBFactory().getPortfolioValueRankDB()).thenReturn(portfolioValueRankDB);

        when(getJwtSecurity().validateToken(eq("token"))).thenReturn(Optional.of(getUser().getId()));

        Response response = target("/user/portfolio/rank").request().header(AUTHORIZATION, "Bearer token").get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(collection, response.readEntity(PortfolioValueRankCollection.class));
    }
}
