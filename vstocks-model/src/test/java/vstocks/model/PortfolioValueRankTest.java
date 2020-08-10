package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;

public class PortfolioValueRankTest {
    @Test
    public void testGettersAndSetters() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(now)
                .setRank(1234);

        assertEquals(userId, portfolioValueRank.getUserId());
        assertEquals(now, portfolioValueRank.getTimestamp());
        assertEquals(1234, portfolioValueRank.getRank());
    }

    @Test
    public void testEquals() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(userId).setTimestamp(now).setRank(1234);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(userId).setTimestamp(now).setRank(4321);
        assertEquals(portfolioValueRank1, portfolioValueRank2);
    }

    @Test
    public void testHashCode() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant timestamp = Instant.parse("2020-08-10T01:02:03.00Z");
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(timestamp)
                .setRank(1234);
        assertEquals(1045571718, portfolioValueRank.hashCode());
    }

    @Test
    public void testToString() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(now)
                .setRank(1234);
        assertEquals("PortfolioValueRank{userId='" + userId + "', timestamp=" + now + ", rank=1234}",
                portfolioValueRank.toString());
    }
}
