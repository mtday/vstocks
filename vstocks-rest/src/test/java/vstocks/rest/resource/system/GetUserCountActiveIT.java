package vstocks.rest.resource.system;

import org.junit.Test;
import vstocks.db.UserCountDB;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.system.UserCount;
import vstocks.model.system.UserCountCollection;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.List;
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

public class GetUserCountActiveIT extends ResourceTest {
    @Test
    public void testUserCountWithData() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserCount userCount1 = new UserCount().setTimestamp(now.minusSeconds(10)).setUsers(1234L);
        UserCount userCount2 = new UserCount().setTimestamp(now.minusSeconds(20)).setUsers(1230L);
        List<UserCount> userCounts = asList(userCount1, userCount2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        UserCountCollection userCountCollection = new UserCountCollection().setUserCounts(userCounts).setDeltas(deltas);

        UserCountDB userCountDB = mock(UserCountDB.class);
        when(userCountDB.getLatestActive()).thenReturn(userCountCollection);
        when(getDBFactory().getUserCountDB()).thenReturn(userCountDB);

        Response response = target("/system/user-count/active").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(userCountCollection, response.readEntity(UserCountCollection.class));
    }
}
