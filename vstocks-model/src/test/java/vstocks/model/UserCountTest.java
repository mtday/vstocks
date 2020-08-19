package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class UserCountTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(1234L);

        assertEquals(now, userCount.getTimestamp());
        assertEquals(1234, userCount.getUsers());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserCount userCount1 = new UserCount().setTimestamp(now).setUsers(1234L);
        UserCount userCount2 = new UserCount().setTimestamp(now).setUsers(1234L);
        assertEquals(userCount1, userCount2);
    }

    @Test
    public void testHashCode() {
        UserCount userCount = new UserCount().setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z")).setUsers(1234L);
        assertEquals(961, new UserCount().hashCode());
        assertEquals(-2031944344, userCount.hashCode());
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        UserCount userCount = new UserCount().setTimestamp(now).setUsers(1234L);
        assertEquals("UserCount{timestamp=" + now + ", users=1234}", userCount.toString());
    }
}
