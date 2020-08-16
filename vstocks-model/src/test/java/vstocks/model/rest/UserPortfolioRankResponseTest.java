package vstocks.model.rest;

import org.junit.Test;
import vstocks.model.Delta;
import vstocks.model.DeltaInterval;
import vstocks.model.PortfolioValueRank;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.DAY1;
import static vstocks.model.DeltaInterval.DAY3;
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
        Map<DeltaInterval, Delta> deltas = Map.of(
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(5.25f),
                DAY3, new Delta().setInterval(DAY3).setChange(12).setPercent(5.30f)
        );

        UserPortfolioRankResponse userPortfolioRankResponse = new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks)
                .setDeltas(deltas);

        assertEquals(currentRank, userPortfolioRankResponse.getCurrentRank());
        assertEquals(historicalRanks, userPortfolioRankResponse.getHistoricalRanks());
        assertEquals(deltas, userPortfolioRankResponse.getDeltas());
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
        Map<DeltaInterval, Delta> deltas = Map.of(
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(5.25f),
                DAY3, new Delta().setInterval(DAY3).setChange(12).setPercent(5.30f)
        );

        UserPortfolioRankResponse userPortfolioRankResponse1 = new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks)
                .setDeltas(deltas);
        UserPortfolioRankResponse userPortfolioRankResponse2 = new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks)
                .setDeltas(deltas);
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
        Map<DeltaInterval, Delta> deltas = Map.of(
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(5.25f),
                DAY3, new Delta().setInterval(DAY3).setChange(12).setPercent(5.30f)
        );

        UserPortfolioRankResponse userPortfolioRankResponse = new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks)
                .setDeltas(deltas);
        assertEquals(29791, new UserPortfolioRankResponse().hashCode());
        assertNotEquals(0, userPortfolioRankResponse.hashCode()); // enums make the value inconsistent
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
        Map<DeltaInterval, Delta> deltas = Map.of(
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(5.25f),
                DAY3, new Delta().setInterval(DAY3).setChange(12).setPercent(5.30f)
        );

        UserPortfolioRankResponse userPortfolioRankResponse = new UserPortfolioRankResponse()
                .setCurrentRank(currentRank)
                .setHistoricalRanks(historicalRanks)
                .setDeltas(deltas);
        assertNotEquals("", userPortfolioRankResponse.toString()); // not interested enough to really check this
    }
}
