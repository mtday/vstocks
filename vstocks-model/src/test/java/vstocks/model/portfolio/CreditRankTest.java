package vstocks.model.portfolio;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class CreditRankTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        CreditRank creditRank = new CreditRank()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);

        assertEquals(1, creditRank.getBatch());
        assertEquals("userId", creditRank.getUserId());
        assertEquals(now, creditRank.getTimestamp());
        assertEquals(20, creditRank.getRank());
    }

    @Test
    public void testEquals() {
        CreditRank creditRank1 = new CreditRank()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);
        CreditRank creditRank2 = new CreditRank()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);
        assertEquals(creditRank1, creditRank2);
    }

    @Test
    public void testHashCode() {
        CreditRank creditRank = new CreditRank()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(timestamp)
                .setRank(20);
        assertEquals(923521, new CreditRank().hashCode());
        assertEquals(-1988764104, creditRank.hashCode());
    }

    @Test
    public void testToString() {
        CreditRank creditRank = new CreditRank()
                .setBatch(1)
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);
        assertEquals("CreditRank{batch=1, userId='userId', timestamp=" + now + ", rank=20}",
                creditRank.toString());
    }
}
