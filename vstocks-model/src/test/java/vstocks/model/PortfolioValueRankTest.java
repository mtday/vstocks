package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.*;
import static vstocks.model.User.generateId;

public class PortfolioValueRankTest {
    @Test
    public void testGettersAndSetters() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(now)
                .setRank(1234)
                .setDeltas(deltas);

        assertEquals(userId, portfolioValueRank.getUserId());
        assertEquals(now, portfolioValueRank.getTimestamp());
        assertEquals(1234, portfolioValueRank.getRank());
        assertEquals(deltas, portfolioValueRank.getDeltas());
    }

    @Test
    public void testEquals() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(now)
                .setRank(1234)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(now)
                .setRank(1234)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals(portfolioValueRank1, portfolioValueRank2);
    }

    @Test
    public void testHashCode() {
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(generateId("user@domain.com"))
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setRank(1234)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals(923521, new PortfolioValueRank().hashCode());
        assertNotEquals(0, portfolioValueRank.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        String userId = generateId("user@domain.com");
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueRank portfolioValueRank = new PortfolioValueRank()
                .setUserId(userId)
                .setTimestamp(now)
                .setRank(1234)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals("PortfolioValueRank{userId='" + userId + "', timestamp=" + now + ", rank=1234, "
                        + "deltas={6h=Delta{interval=6h, change=5, percent=5.25}, 12h=Delta{interval=12h, change=5, "
                        + "percent=5.25}, 1d=Delta{interval=1d, change=10, percent=10.25}}}",
                portfolioValueRank.toString());
    }
}
