package vstocks.rest.resource.system;

import org.junit.Test;
import vstocks.db.UserCountDB;
import vstocks.model.Delta;
import vstocks.model.UserCount;
import vstocks.rest.ResourceTest;

import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static javax.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.DeltaInterval.*;

public class GetUserCountIT extends ResourceTest {
    @Test
    public void testValueSummaryWithData() {
        UserCount userCount = new UserCount()
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setUsers(20_000)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));

        UserCountDB userCountDB = mock(UserCountDB.class);
        when(userCountDB.getLatest()).thenReturn(userCount);
        when(getDBFactory().getUserCountDB()).thenReturn(userCountDB);

        Response response = target("/system/user-count").request().get();

        assertEquals(OK.getStatusCode(), response.getStatus());
        assertEquals(APPLICATION_JSON, response.getHeaderString(CONTENT_TYPE));

        assertEquals(userCount, response.readEntity(UserCount.class));
    }
}
