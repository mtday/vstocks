package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.User.generateId;

public class PortfolioValueCollectionTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValue portfolioValue1 = new PortfolioValue()
                .setUserId(generateId("user1@domain.com"))
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        PortfolioValue portfolioValue2 = new PortfolioValue()
                .setUserId(generateId("user2@domain.com"))
                .setTimestamp(now)
                .setCredits(1230L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1240L);

        List<PortfolioValue> values = asList(portfolioValue1, portfolioValue2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueCollection portfolioValueCollection = new PortfolioValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(values, portfolioValueCollection.getValues());
        assertEquals(deltas, portfolioValueCollection.getDeltas());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValue portfolioValue1 = new PortfolioValue()
                .setUserId(generateId("user1@domain.com"))
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        PortfolioValue portfolioValue2 = new PortfolioValue()
                .setUserId(generateId("user2@domain.com"))
                .setTimestamp(now)
                .setCredits(1230L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1240L);

        List<PortfolioValue> values = asList(portfolioValue1, portfolioValue2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueCollection portfolioValueCollection1 = new PortfolioValueCollection().setValues(values).setDeltas(deltas);
        PortfolioValueCollection portfolioValueCollection2 = new PortfolioValueCollection().setValues(values).setDeltas(deltas);

        assertEquals(portfolioValueCollection1, portfolioValueCollection2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValue portfolioValue1 = new PortfolioValue()
                .setUserId(generateId("user1@domain.com"))
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        PortfolioValue portfolioValue2 = new PortfolioValue()
                .setUserId(generateId("user2@domain.com"))
                .setTimestamp(now)
                .setCredits(1230L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1240L);

        List<PortfolioValue> values = asList(portfolioValue1, portfolioValue2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueCollection portfolioValueCollection = new PortfolioValueCollection().setValues(values).setDeltas(deltas);

        assertNotEquals(0, new PortfolioValueCollection().hashCode()); // enums make the value inconsistent
        assertNotEquals(0, portfolioValueCollection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValue portfolioValue1 = new PortfolioValue()
                .setUserId(generateId("user1@domain.com"))
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        PortfolioValue portfolioValue2 = new PortfolioValue()
                .setUserId(generateId("user2@domain.com"))
                .setTimestamp(now)
                .setCredits(1230L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1240L);

        List<PortfolioValue> values = asList(portfolioValue1, portfolioValue2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueCollection portfolioValueCollection =
                new PortfolioValueCollection().setValues(values).setDeltas(deltas);

        assertEquals("PortfolioValueCollection{values=[" + portfolioValue1 + ", " + portfolioValue2
                + "], deltas=" + deltas + "}", portfolioValueCollection.toString());
    }
}
