package vstocks.model.rest;

import org.junit.Test;
import vstocks.model.PortfolioValueRank;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.User.generateId;

public class UserPortfolioRankResponseTest {
    @Test
    public void testGettersAndSetters() {
        List<PortfolioValueRank> historicalRanks = Stream.iterate(1, i -> i + 1).limit(10)
                .map(i -> new PortfolioValueRank()
                        .setUserId(generateId("user@domain.com"))
                        .setRank(i * 10)
                        .setTimestamp(Instant.now().minusSeconds(i * 10).truncatedTo(SECONDS)))
                .collect(toList());
        PortfolioValueRank currentRank = historicalRanks.iterator().next();

        UserPortfolioRankResponse userPortfolioRankResponse = new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks);

        assertEquals(currentRank, userPortfolioRankResponse.getCurrentRank());
        assertEquals(historicalRanks, userPortfolioRankResponse.getHistoricalRanks());
    }

    @Test
    public void testEquals() {
        List<PortfolioValueRank> historicalRanks = Stream.iterate(1, i -> i + 1).limit(10)
                .map(i -> new PortfolioValueRank()
                        .setUserId(generateId("user@domain.com"))
                        .setRank(i * 10)
                        .setTimestamp(Instant.now().minusSeconds(i * 10).truncatedTo(SECONDS)))
                .collect(toList());
        PortfolioValueRank currentRank = historicalRanks.iterator().next();

        UserPortfolioRankResponse userPortfolioRankResponse1 = new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks);
        UserPortfolioRankResponse userPortfolioRankResponse2 = new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks);
        assertEquals(userPortfolioRankResponse1, userPortfolioRankResponse2);
    }

    @Test
    public void testHashCode() {
        List<PortfolioValueRank> historicalRanks = Stream.iterate(1, i -> i + 1).limit(10)
                .map(i -> new PortfolioValueRank()
                        .setUserId(generateId("user@domain.com"))
                        .setRank(i * 10)
                        .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z")))
                .collect(toList());
        PortfolioValueRank currentRank = historicalRanks.iterator().next();

        UserPortfolioRankResponse userPortfolioRankResponse = new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks);
        assertEquals(961, new UserPortfolioRankResponse().hashCode());
        assertEquals(-2087956112, userPortfolioRankResponse.hashCode());
    }

    @Test
    public void testToString() {
        String userId = generateId("user@domain.com");
        List<PortfolioValueRank> historicalRanks = singletonList(
                new PortfolioValueRank()
                        .setUserId(generateId("user@domain.com"))
                        .setRank(10)
                        .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
        );
        PortfolioValueRank currentRank = historicalRanks.iterator().next();

        UserPortfolioRankResponse userPortfolioRankResponse = new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks);
        assertEquals("UserPortfolioRankResponse{currentRank=PortfolioValueRank{userId='" + userId + "', "
                + "timestamp=2020-08-10T01:02:03Z, rank=10}, historicalRanks=[PortfolioValueRank{userId='" + userId
                + "', timestamp=2020-08-10T01:02:03Z, rank=10}]}", userPortfolioRankResponse.toString());
    }
}
