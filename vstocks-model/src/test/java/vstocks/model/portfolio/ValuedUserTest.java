package vstocks.model.portfolio;

import org.junit.Test;
import vstocks.model.User;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class ValuedUserTest {
    private final User user = new User()
            .setId(User.generateId("user@domain.com"))
            .setEmail("user@domain.com")
            .setUsername("user")
            .setDisplayName("User")
            .setProfileImage("link");
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        ValuedUser valuedUser = new ValuedUser()
                .setUser(user)
                .setBatch(1)
                .setTimestamp(now)
                .setValue(20);

        assertEquals(user, valuedUser.getUser());
        assertEquals(1, valuedUser.getBatch());
        assertEquals(now, valuedUser.getTimestamp());
        assertEquals(20, valuedUser.getValue());
    }

    @Test
    public void testEquals() {
        ValuedUser valuedUser1 = new ValuedUser()
                .setUser(user)
                .setBatch(1)
                .setTimestamp(now)
                .setValue(20);
        ValuedUser valuedUser2 = new ValuedUser()
                .setUser(user)
                .setBatch(1)
                .setTimestamp(now)
                .setValue(20);
        assertEquals(valuedUser1, valuedUser2);
    }

    @Test
    public void testHashCode() {
        ValuedUser valuedUser = new ValuedUser()
                .setUser(user)
                .setBatch(1)
                .setTimestamp(timestamp)
                .setValue(20);
        assertEquals(923521, new ValuedUser().hashCode());
        assertEquals(-55065663, valuedUser.hashCode());
    }

    @Test
    public void testToString() {
        ValuedUser valuedUser = new ValuedUser()
                .setUser(user)
                .setBatch(1)
                .setTimestamp(now)
                .setValue(20);
        assertEquals("ValuedUser{user=" + user + ", batch=1, timestamp=" + now + ", value=20}",
                valuedUser.toString());
    }
}
