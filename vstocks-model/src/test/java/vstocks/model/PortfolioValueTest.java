package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;

public class PortfolioValueTest {
    @Test
    public void testGettersAndSetters() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(userId)
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);

        assertEquals(userId, portfolioValue.getUserId());
        assertEquals(now, portfolioValue.getTimestamp());
        assertEquals(1234, portfolioValue.getCredits());
        assertEquals(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)), portfolioValue.getMarketValues());
        assertEquals(1244, portfolioValue.getTotal());

        assertEquals(0, PortfolioValue.FULL_COMPARATOR.compare(portfolioValue, portfolioValue));
        assertEquals(0, PortfolioValue.UNIQUE_COMPARATOR.compare(portfolioValue, portfolioValue));
    }

    @Test
    public void testEquals() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValue portfolioValue1 = new PortfolioValue().setUserId(userId).setTimestamp(now).setTotal(1234);
        PortfolioValue portfolioValue2 = new PortfolioValue().setUserId(userId).setTimestamp(now).setTotal(4321);
        assertEquals(portfolioValue1, portfolioValue2);
    }

    @Test
    public void testHashCode() {
        String userId = User.generateId("user@domain.com");
        Instant timestamp = Instant.parse("2020-08-10T01:02:03.00Z");
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(userId)
                .setTimestamp(timestamp)
                .setCredits(1234L)
                .setMarketValues(singletonMap(TWITTER, 10L))
                .setTotal(1244L);
        assertEquals(1045571718, portfolioValue.hashCode());
    }

    @Test
    public void testToString() {
        String userId = User.generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(userId)
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(singletonMap(TWITTER, 10L))
                .setTotal(1244L);
        assertEquals("PortfolioValue{userId='" + userId + "', timestamp=" + now
                        + ", credits=1234, marketValues={TWITTER=10}, total=1244}", portfolioValue.toString());
    }
}
