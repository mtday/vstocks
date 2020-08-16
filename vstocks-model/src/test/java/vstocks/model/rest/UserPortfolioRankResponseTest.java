package vstocks.model.rest;

import org.junit.Test;
import vstocks.model.PortfolioValueRank;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
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
}
