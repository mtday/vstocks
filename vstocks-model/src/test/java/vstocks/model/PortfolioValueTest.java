package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.*;
import static vstocks.model.DeltaInterval.DAY1;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.User.generateId;

public class PortfolioValueTest {
    @Test
    public void testGettersAndSetters() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(userId)
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L)
                .setDeltas(deltas);

        assertEquals(userId, portfolioValue.getUserId());
        assertEquals(now, portfolioValue.getTimestamp());
        assertEquals(1234, portfolioValue.getCredits());
        assertEquals(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)), portfolioValue.getMarketValues());
        assertEquals(1244, portfolioValue.getTotal());
        assertEquals(deltas, portfolioValue.getDeltas());
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
                .setTotal(1244L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        PortfolioValue portfolioValue2 = new PortfolioValue()
                .setUserId(userId)
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals(portfolioValue1, portfolioValue2);
    }

    @Test
    public void testHashCode() {
        PortfolioValue portfolioValue = new PortfolioValue()
                .setUserId(generateId("user@domain.com"))
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setCredits(1234L)
                .setMarketValues(Map.of(TWITTER, 10L, YOUTUBE, 20L))
                .setTotal(1244L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
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
                .setTotal(1264L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals("PortfolioValue{userId='" + userId + "', timestamp=" + now + ", credits=1234, marketValues="
                + "{TWITTER=10, YOUTUBE=20}, total=1264, deltas={6h=Delta{interval=6h, change=5, "
                + "percent=5.25}, 12h=Delta{interval=12h, change=5, percent=5.25}, 1d=Delta{interval=1d, "
                + "change=10, percent=10.25}}}", portfolioValue.toString());
    }
}
