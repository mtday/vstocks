package vstocks.model.rest;

import org.junit.Test;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.PortfolioValue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.DAY1;
import static vstocks.model.DeltaInterval.DAY3;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.User.generateId;

public class UserPortfolioValueResponseTest {
    @Test
    public void testGettersAndSetters() {
        List<PortfolioValue> historicalValues = Stream.iterate(1, i -> i + 1).limit(10)
                .map(i -> new PortfolioValue()
                        .setUserId(generateId("user@domain.com"))
                        .setCredits(i * 10)
                        .setMarketValues(Map.of(TWITTER, i * 10L, YOUTUBE, i * 10L))
                        .setTotal(i * 30)
                        .setTimestamp(Instant.now().minusSeconds(i * 10).truncatedTo(SECONDS)))
                .collect(toList());
        PortfolioValue currentValue = historicalValues.iterator().next();
        Map<DeltaInterval, Delta> deltas = Map.of(
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(5.25f),
                DAY3, new Delta().setInterval(DAY3).setChange(12).setPercent(5.30f)
        );

        UserPortfolioValueResponse userPortfolioValueResponse = new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues)
                .setDeltas(deltas);

        assertEquals(currentValue, userPortfolioValueResponse.getCurrentValue());
        assertEquals(historicalValues, userPortfolioValueResponse.getHistoricalValues());
        assertEquals(deltas, userPortfolioValueResponse.getDeltas());
    }

    @Test
    public void testEquals() {
        List<PortfolioValue> historicalValues = Stream.iterate(1, i -> i + 1).limit(10)
                .map(i -> new PortfolioValue()
                        .setUserId(generateId("user@domain.com"))
                        .setCredits(i * 10)
                        .setMarketValues(Map.of(TWITTER, i * 10L, YOUTUBE, i * 10L))
                        .setTotal(i * 30)
                        .setTimestamp(Instant.now().minusSeconds(i * 10).truncatedTo(SECONDS)))
                .collect(toList());
        PortfolioValue currentValue = historicalValues.iterator().next();
        Map<DeltaInterval, Delta> deltas = Map.of(
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(5.25f),
                DAY3, new Delta().setInterval(DAY3).setChange(12).setPercent(5.30f)
        );

        UserPortfolioValueResponse userPortfolioValueResponse1 = new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues)
                .setDeltas(deltas);
        UserPortfolioValueResponse userPortfolioValueResponse2 = new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues)
                .setDeltas(deltas);
        assertEquals(userPortfolioValueResponse1, userPortfolioValueResponse2);
    }

    @Test
    public void testHashCode() {
        List<PortfolioValue> historicalValues = Stream.iterate(1, i -> i + 1).limit(10)
                .map(i -> new PortfolioValue()
                        .setUserId(generateId("user@domain.com"))
                        .setCredits(i * 10)
                        .setMarketValues(new TreeMap<>(Map.of(TWITTER, i * 10L, YOUTUBE, i * 10L)))
                        .setTotal(i * 30)
                        .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z")))
                .collect(toList());
        PortfolioValue currentValue = historicalValues.iterator().next();
        Map<DeltaInterval, Delta> deltas = Map.of(
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(5.25f),
                DAY3, new Delta().setInterval(DAY3).setChange(12).setPercent(5.30f)
        );

        UserPortfolioValueResponse userPortfolioValueResponse = new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues)
                .setDeltas(deltas);
        assertEquals(29791, new UserPortfolioValueResponse().hashCode());
        assertNotEquals(0, userPortfolioValueResponse.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        String userId = generateId("user@domain.com");
        List<PortfolioValue> historicalValues = singletonList(
                new PortfolioValue()
                        .setUserId(userId)
                        .setCredits(10)
                        .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 10L)))
                        .setTotal(30)
                        .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
        );
        PortfolioValue currentValue = historicalValues.iterator().next();
        Map<DeltaInterval, Delta> deltas = Map.of(
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(5.25f),
                DAY3, new Delta().setInterval(DAY3).setChange(12).setPercent(5.30f)
        );

        UserPortfolioValueResponse userPortfolioValueResponse = new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues)
                .setDeltas(deltas);
        assertNotEquals("", userPortfolioValueResponse.toString()); // not interested enough to really check this
    }
}
