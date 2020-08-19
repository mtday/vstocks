package vstocks.rest.resource.system;

import org.junit.Test;
import vstocks.db.PortfolioValueSummaryDB;
import vstocks.model.Delta;
import vstocks.model.Market;
import vstocks.model.PortfolioValueSummary;
import vstocks.model.PortfolioValueSummaryCollection;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.DeltaInterval.*;

public class GetValueSummaryIT extends ResourceTest {
    @Test
    public void testValueSummaryWithData() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 5_000L));

        PortfolioValueSummary summary1 = new PortfolioValueSummary()
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setCredits(20_000)
                .setMarketValues(marketValues)
                .setTotal(45_000);
        PortfolioValueSummary summary2 = new PortfolioValueSummary()
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setCredits(20_000)
                .setMarketValues(marketValues)
                .setTotal(45_000);

        PortfolioValueSummaryCollection collection = new PortfolioValueSummaryCollection()
                .setSummaries(asList(summary1, summary2))
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));

        PortfolioValueSummaryDB portfolioValueSummaryDB = mock(PortfolioValueSummaryDB.class);
        when(portfolioValueSummaryDB.getLatest()).thenReturn(collection);
        when(getDBFactory().getPortfolioValueSummaryDB()).thenReturn(portfolioValueSummaryDB);

        Response response = target("/system/value-summary").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(collection, response.readEntity(PortfolioValueSummaryCollection.class));
    }
}
