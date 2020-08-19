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

public class PortfolioValueSummaryCollectionTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueSummary portfolioValueSummary1 = new PortfolioValueSummary()
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        PortfolioValueSummary portfolioValueSummary2 = new PortfolioValueSummary()
                .setTimestamp(now)
                .setCredits(1230L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1240L);

        List<PortfolioValueSummary> summaries = asList(portfolioValueSummary1, portfolioValueSummary2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueSummaryCollection collection =
                new PortfolioValueSummaryCollection().setSummaries(summaries).setDeltas(deltas);

        assertEquals(summaries, collection.getSummaries());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueSummary portfolioValueSummary1 = new PortfolioValueSummary()
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        PortfolioValueSummary portfolioValueSummary2 = new PortfolioValueSummary()
                .setTimestamp(now)
                .setCredits(1230L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1240L);

        List<PortfolioValueSummary> summaries = asList(portfolioValueSummary1, portfolioValueSummary2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueSummaryCollection collection1 =
                new PortfolioValueSummaryCollection().setSummaries(summaries).setDeltas(deltas);
        PortfolioValueSummaryCollection collection2 =
                new PortfolioValueSummaryCollection().setSummaries(summaries).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueSummary portfolioValueSummary1 = new PortfolioValueSummary()
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        PortfolioValueSummary portfolioValueSummary2 = new PortfolioValueSummary()
                .setTimestamp(now)
                .setCredits(1230L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1240L);

        List<PortfolioValueSummary> summaries = asList(portfolioValueSummary1, portfolioValueSummary2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueSummaryCollection collection =
                new PortfolioValueSummaryCollection().setSummaries(summaries).setDeltas(deltas);

        assertEquals(923521, new PortfolioValueSummary().hashCode());
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueSummary portfolioValueSummary1 = new PortfolioValueSummary()
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        PortfolioValueSummary portfolioValueSummary2 = new PortfolioValueSummary()
                .setTimestamp(now)
                .setCredits(1230L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1240L);

        List<PortfolioValueSummary> summaries = asList(portfolioValueSummary1, portfolioValueSummary2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        PortfolioValueSummaryCollection collection =
                new PortfolioValueSummaryCollection().setSummaries(summaries).setDeltas(deltas);

        assertEquals("PortfolioValueSummaryCollection{summaries=[" + portfolioValueSummary1 + ", "
                + portfolioValueSummary2 + "], deltas=" + deltas + "}", collection.toString());
    }
}
