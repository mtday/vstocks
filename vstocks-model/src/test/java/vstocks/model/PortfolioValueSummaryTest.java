package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;

public class PortfolioValueSummaryTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary()
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);

        assertEquals(now, portfolioValueSummary.getTimestamp());
        assertEquals(1234, portfolioValueSummary.getCredits());
        assertEquals(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)), portfolioValueSummary.getMarketValues());
        assertEquals(1244, portfolioValueSummary.getTotal());
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
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        assertEquals(portfolioValueSummary1, portfolioValueSummary2);
    }

    @Test
    public void testHashCode() {
        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary()
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setCredits(1234L)
                .setMarketValues(Map.of(TWITTER, 10L, YOUTUBE, 20L))
                .setTotal(1244L);
        assertNotEquals(0, new PortfolioValueSummary().hashCode()); // enums make the value inconsistent
        assertNotEquals(0, portfolioValueSummary.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary()
                .setTimestamp(now)
                .setCredits(1234L)
                .setMarketValues(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1264L);
        assertEquals("PortfolioValueSummary{timestamp=" + now + ", credits=1234, marketValues="
                + "{Twitter=10, YouTube=20}, total=1264}", portfolioValueSummary.toString());
    }
}
