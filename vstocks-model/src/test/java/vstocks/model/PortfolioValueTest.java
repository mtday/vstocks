package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static vstocks.model.Market.TWITTER;

public class PortfolioValueTest {
    @Test
    public void testGettersAndSetters() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(userId)
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(singletonMap(TWITTER, 10L))
                .setTotal(1244L);

        assertEquals(userId, portfolioValue.getUserId());
        assertEquals(now, portfolioValue.getTimestamp());
        assertEquals(1234, portfolioValue.getCredits());
        assertEquals(singletonMap(TWITTER, 10L), portfolioValue.getMarketValues());
        assertEquals(1244, portfolioValue.getTotal());
    }

    @Test
    public void testEquals() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PortfolioValue portfolioValue1 = new PortfolioValue().setUserId(userId).setTimestamp(now).setTotal(1234);
        PortfolioValue portfolioValue2 = new PortfolioValue().setUserId(userId).setTimestamp(now).setTotal(4321);
        assertEquals(portfolioValue1, portfolioValue2);
    }

    @Test
    public void testHashCode() {
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
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
        String userId = UUID.nameUUIDFromBytes("user@domain.com".getBytes(UTF_8)).toString();
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
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
