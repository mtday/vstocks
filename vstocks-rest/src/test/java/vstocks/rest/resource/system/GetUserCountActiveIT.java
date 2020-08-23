package vstocks.rest.resource.system;

import org.junit.Test;
import vstocks.db.system.ActiveUserCountService;
import vstocks.model.Delta;
import vstocks.model.system.ActiveUserCount;
import vstocks.model.system.ActiveUserCountCollection;
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

public class GetUserCountActiveIT extends ResourceTest {
    @Test
    public void testActiveUserCountWithData() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        ActiveUserCount userCount1 = new ActiveUserCount().setTimestamp(now.minusSeconds(10)).setCount(1234L);
        ActiveUserCount userCount2 = new ActiveUserCount().setTimestamp(now.minusSeconds(20)).setCount(1230L);
        List<ActiveUserCount> activeUserCounts = asList(userCount1, userCount2);

        ActiveUserCountCollection activeUserCountCollection = new ActiveUserCountCollection()
                .setCounts(activeUserCounts)
                .setDeltas(Delta.getDeltas(activeUserCounts, ActiveUserCount::getTimestamp, ActiveUserCount::getCount));

        ActiveUserCountService activeUserCountService = mock(ActiveUserCountService.class);
        when(activeUserCountService.getLatest()).thenReturn(activeUserCountCollection);
        when(getServiceFactory().getActiveUserCountService()).thenReturn(activeUserCountService);

        Response response = target("/system/user-count/active").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(activeUserCountCollection, response.readEntity(ActiveUserCountCollection.class));
    }
}
