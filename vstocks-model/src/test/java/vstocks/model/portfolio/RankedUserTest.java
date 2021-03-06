package vstocks.model.portfolio;

import org.junit.Test;
import vstocks.model.User;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class RankedUserTest {
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
        RankedUser rankedUser = new RankedUser()
                .setUser(user)
                .setBatch(1)
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);

        assertEquals(user, rankedUser.getUser());
        assertEquals(1, rankedUser.getBatch());
        assertEquals(now, rankedUser.getTimestamp());
        assertEquals(20, rankedUser.getRank());
        assertEquals(10, rankedUser.getValue());
    }

    @Test
    public void testEquals() {
        RankedUser rankedUser1 = new RankedUser()
                .setUser(user)
                .setBatch(1)
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);
        RankedUser rankedUser2 = new RankedUser()
                .setUser(user)
                .setBatch(1)
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);
        assertEquals(rankedUser1, rankedUser2);
    }

    @Test
    public void testHashCode() {
        RankedUser rankedUser = new RankedUser()
                .setUser(user)
                .setBatch(1)
                .setTimestamp(timestamp)
                .setRank(20)
                .setValue(10);
        assertEquals(-1707035543, rankedUser.hashCode());
    }

    @Test
    public void testToString() {
        RankedUser rankedUser = new RankedUser()
                .setUser(user)
                .setBatch(1)
                .setTimestamp(now)
                .setRank(20)
                .setValue(10);
        assertEquals("RankedUser{user=" + user + ", batch=1, timestamp=" + now + ", rank=20, value=10}",
                rankedUser.toString());
    }
}
