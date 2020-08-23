package vstocks.rest.resource.system;

import org.junit.Test;
import vstocks.db.system.ActiveTransactionCountService;
import vstocks.model.system.ActiveTransactionCount;
import vstocks.model.system.ActiveTransactionCountCollection;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Delta.getDeltas;

public class GetActiveTransactionCountIT extends ResourceTest {
    @Test
    public void testActiveTransactionCountWithData() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        ActiveTransactionCount txCount1 = new ActiveTransactionCount().setTimestamp(now.minusSeconds(10)).setCount(12L);
        ActiveTransactionCount txCount2 = new ActiveTransactionCount().setTimestamp(now.minusSeconds(20)).setCount(13L);
        List<ActiveTransactionCount> activeTxCounts = asList(txCount1, txCount2);

        ActiveTransactionCountCollection activeTxCountCollection = new ActiveTransactionCountCollection()
                .setCounts(activeTxCounts)
                .setDeltas(getDeltas(activeTxCounts, ActiveTransactionCount::getTimestamp, ActiveTransactionCount::getCount));

        ActiveTransactionCountService activeTxCountService = mock(ActiveTransactionCountService.class);
        when(activeTxCountService.getLatest()).thenReturn(activeTxCountCollection);
        when(getServiceFactory().getActiveTransactionCountService()).thenReturn(activeTxCountService);

        Response response = target("/system/transaction-count/active").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(activeTxCountCollection, response.readEntity(ActiveTransactionCountCollection.class));
    }
}
