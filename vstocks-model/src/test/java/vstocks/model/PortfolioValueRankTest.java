package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class PortfolioValueRankTest {
    @Test
    public void testGettersAndSetters() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(now)
                .setRank(1234);

        assertEquals(userId, portfolioValueRank.getUserId());
        assertEquals(now, portfolioValueRank.getTimestamp());
        assertEquals(1234, portfolioValueRank.getRank());

        assertEquals(0, PortfolioValueRank.FULL_COMPARATOR.compare(portfolioValueRank, portfolioValueRank));
        assertEquals(0, PortfolioValueRank.UNIQUE_COMPARATOR.compare(portfolioValueRank, portfolioValueRank));
    }

    @Test
    public void testEquals() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(userId).setTimestamp(now).setRank(1234);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(userId).setTimestamp(now).setRank(4321);
        assertEquals(portfolioValueRank1, portfolioValueRank2);
    }

    @Test
    public void testHashCode() {
        String userId = User.generateId("user@domain.com");
        Instant timestamp = Instant.parse("2020-08-10T01:02:03.00Z");
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(timestamp)
                .setRank(1234);
        assertEquals(1045571718, portfolioValueRank.hashCode());
    }

    @Test
    public void testToString() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(now)
                .setRank(1234);
        assertEquals("PortfolioValueRank{userId='" + userId + "', timestamp=" + now + ", rank=1234}",
                portfolioValueRank.toString());
    }
}
