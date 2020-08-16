package vstocks.model.rest;

import org.junit.Test;
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

        UserPortfolioValueResponse userPortfolioValueResponse = new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues);

        assertEquals(currentValue, userPortfolioValueResponse.getCurrentValue());
        assertEquals(historicalValues, userPortfolioValueResponse.getHistoricalValues());
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

        UserPortfolioValueResponse userPortfolioValueResponse1 = new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues);
        UserPortfolioValueResponse userPortfolioValueResponse2 = new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues);
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

        UserPortfolioValueResponse userPortfolioValueResponse = new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues);
        assertEquals(961, new UserPortfolioValueResponse().hashCode());
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

        UserPortfolioValueResponse userPortfolioValueResponse = new UserPortfolioValueResponse()
                .setCurrentValue(currentValue)
                .setHistoricalValues(historicalValues);
        assertEquals("UserPortfolioValueResponse{currentValue=PortfolioValue{userId='" + userId + "', "
                        + "timestamp=2020-08-10T01:02:03Z, credits=10, marketValues={TWITTER=10, YOUTUBE=10}, "
                        + "total=30}, historicalValues=[PortfolioValue{userId='" + userId + "', "
                        + "timestamp=2020-08-10T01:02:03Z, credits=10, marketValues={TWITTER=10, YOUTUBE=10}, total=30}]}",
                userPortfolioValueResponse.toString());
    }
}
