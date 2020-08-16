package vstocks.model.rest;

import org.junit.Test;
import vstocks.model.PortfolioValue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
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
}
