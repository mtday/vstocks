package vstocks.rest.resource.standings;

import org.junit.Test;
import vstocks.db.PortfolioValueRankDB;
import vstocks.model.*;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.DatabaseField.RANK;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.DeltaInterval.*;

public class GetRanksIT extends ResourceTest {
    @Test
    public void testStandingsRanksPageAndSort() {
        Page page = new Page().setPage(2).setSize(15);
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(), RANK.toSort()));
        Results<PortfolioValueRank> results =
                new Results<PortfolioValueRank>().setTotal(0).setPage(page).setResults(emptyList());
        PortfolioValueRankDB portfolioValueRankDB = mock(PortfolioValueRankDB.class);
        when(portfolioValueRankDB.getAll(eq(page), eq(sort))).thenReturn(results);
        when(getDBFactory().getPortfolioValueRankDB()).thenReturn(portfolioValueRankDB);

        Response response = target("/standings/ranks")
                .queryParam("pageNum", page.getPage())
                .queryParam("pageSize", page.getSize())
                .queryParam("sort", "USER_ID,RANK")
                .request()
                .get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(results, response.readEntity(new PortfolioValueRankResultsGenericType()));
    }

    @Test
    public void testStandingsRanksWithData() {
        PortfolioValueRank rank1 = new PortfolioValueRank()
                .setUserId(getUser().getId())
                .setRank(1)
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        PortfolioValueRank rank2 = new PortfolioValueRank()
                .setUserId(getUser().getId())
                .setRank(2)
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        Results<PortfolioValueRank> results = new Results<PortfolioValueRank>()
                .setTotal(2)
                .setPage(new Page())
                .setResults(asList(rank1, rank2));
        PortfolioValueRankDB portfolioValueRankDB = mock(PortfolioValueRankDB.class);
        when(portfolioValueRankDB.getAll(any(), any())).thenReturn(results);
        when(getDBFactory().getPortfolioValueRankDB()).thenReturn(portfolioValueRankDB);

        Response response = target("/standings/ranks").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(results, response.readEntity(new PortfolioValueRankResultsGenericType()));
    }
}
