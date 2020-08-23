package vstocks.rest.resource.system;

import org.junit.Test;
import vstocks.db.system.TotalTransactionCountService;
import vstocks.model.system.TotalTransactionCount;
import vstocks.model.system.TotalTransactionCountCollection;
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

public class GetTotalTransactionCountIT extends ResourceTest {
    @Test
    public void testTotalTransactionCountWithData() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TotalTransactionCount txCount1 = new TotalTransactionCount().setTimestamp(now.minusSeconds(10)).setCount(12L);
        TotalTransactionCount txCount2 = new TotalTransactionCount().setTimestamp(now.minusSeconds(20)).setCount(13L);
        List<TotalTransactionCount> totalTxCounts = asList(txCount1, txCount2);

        TotalTransactionCountCollection totalTxCountCollection = new TotalTransactionCountCollection()
                .setCounts(totalTxCounts)
                .setDeltas(getDeltas(totalTxCounts, TotalTransactionCount::getTimestamp, TotalTransactionCount::getCount));

        TotalTransactionCountService totalTxCountService = mock(TotalTransactionCountService.class);
        when(totalTxCountService.getLatest()).thenReturn(totalTxCountCollection);
        when(getServiceFactory().getTotalTransactionCountService()).thenReturn(totalTxCountService);

        Response response = target("/system/transaction-count/total").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(totalTxCountCollection, response.readEntity(TotalTransactionCountCollection.class));
    }
}
