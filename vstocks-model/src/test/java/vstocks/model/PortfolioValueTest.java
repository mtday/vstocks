package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.User.generateId;

public class PortfolioValueTest {
    @Test
    public void testGettersAndSetters() {
        String userId = generateId("user@domain.com");
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
    }

    @Test
    public void testEquals() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValue portfolioValue1 = new PortfolioValue()
                .setUserId(userId)
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        PortfolioValue portfolioValue2 = new PortfolioValue()
                .setUserId(userId)
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        assertEquals(portfolioValue1, portfolioValue2);
    }

    @Test
    public void testHashCode() {
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(generateId("user@domain.com"))
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setCredits(1234L)
                .setMarketValues(Map.of(TWITTER, 10L, YOUTUBE, 20L))
                .setTotal(1244L);
        assertNotEquals(0, new PortfolioValue().hashCode()); // enums make the value inconsistent
        assertNotEquals(0, portfolioValue.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(userId)
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1264L);
        assertEquals("PortfolioValue{userId='" + userId + "', timestamp=" + now + ", credits=1234, marketValues="
                + "{Twitter=10, YouTube=20}, total=1264}", portfolioValue.toString());
    }
}
