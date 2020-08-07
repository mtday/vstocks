package vstocks.rest.resource.v1.user;

import org.junit.Test;
import vstocks.db.UserDB;
import vstocks.rest.ResourceTest;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.OK;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserResetIT extends ResourceTest {
    @Test
    public void testResetUser() {
        UserDB userDB = mock(UserDB.class);
        when(getDBFactory().getUserDB()).thenReturn(userDB);

        Response response = target("/v1/user/reset").request().put(Entity.text(""));
        assertEquals(OK.getStatusCode(), response.getStatus());

        verify(userDB, times(1)).reset(eq(getUser().getId()));
    }
}
