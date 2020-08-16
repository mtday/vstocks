package vstocks.model;

import org.junit.Test;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static vstocks.model.User.generateId;

public class PortfolioValueRankTest {
    @Test
    public void testGettersAndSetters() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
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
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank().setUserId(userId).setTimestamp(now).setRank(1234);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank().setUserId(userId).setTimestamp(now).setRank(1234);
        assertEquals(portfolioValueRank1, portfolioValueRank2);
    }

    @Test
    public void testHashCode() {
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(generateId("user@domain.com"))
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setRank(1234);
        assertEquals(29791, new PortfolioValueRank().hashCode());
        assertEquals(-1947013876, portfolioValueRank.hashCode());
    }

    @Test
    public void testToString() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(now)
                .setRank(1234);
        assertEquals("PortfolioValueRank{userId='" + userId + "', timestamp=" + now + ", rank=1234}",
                portfolioValueRank.toString());
    }
}
