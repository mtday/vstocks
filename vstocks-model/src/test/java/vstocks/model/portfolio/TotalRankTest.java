package vstocks.model.portfolio;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class TotalRankTest {
    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final Instant timestamp = Instant.parse("2020-12-03T10:15:30.00Z");

    @Test
    public void testGettersAndSetters() {
        TotalRank totalRank = new TotalRank()
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);

        assertEquals("userId", totalRank.getUserId());
        assertEquals(now, totalRank.getTimestamp());
        assertEquals(20, totalRank.getRank());
    }

    @Test
    public void testEquals() {
        TotalRank totalRank1 = new TotalRank()
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);
        TotalRank totalRank2 = new TotalRank()
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);
        assertEquals(totalRank1, totalRank2);
    }

    @Test
    public void testHashCode() {
        TotalRank totalRank = new TotalRank()
                .setUserId("userId")
                .setTimestamp(timestamp)
                .setRank(20);
        assertEquals(29791, new TotalRank().hashCode());
        assertEquals(-1989687625, totalRank.hashCode());
    }

    @Test
    public void testToString() {
        TotalRank totalRank = new TotalRank()
                .setUserId("userId")
                .setTimestamp(now)
                .setRank(20);
        assertEquals("TotalRank{userId='userId', timestamp=" + now + ", rank=20}",
                totalRank.toString());
    }
}
