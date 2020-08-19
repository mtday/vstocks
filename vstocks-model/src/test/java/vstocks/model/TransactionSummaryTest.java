package vstocks.model;

import org.junit.Test;

import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static vstocks.model.DeltaInterval.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;

public class TransactionSummaryTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));
        TransactionSummary transactionSummary = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L)
                .setDeltas(deltas);

        assertEquals(now, transactionSummary.getTimestamp());
        assertEquals(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)), transactionSummary.getTransactions());
        assertEquals(1244, transactionSummary.getTotal());
        assertEquals(deltas, transactionSummary.getDeltas());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionSummary transactionSummary1 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        TransactionSummary transactionSummary2 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals(transactionSummary1, transactionSummary2);
    }

    @Test
    public void testHashCode() {
        TransactionSummary transactionSummary = new TransactionSummary()
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setTransactions(Map.of(TWITTER, 10L, YOUTUBE, 20L))
                .setTotal(1244L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertNotEquals(0, new TransactionSummary().hashCode()); // enums make the value inconsistent
        assertNotEquals(0, transactionSummary.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionSummary transactionSummary = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1264L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals("TransactionSummary{timestamp=" + now + ", transactions={Twitter=10, YouTube=20}, total=1264, "
                + "deltas={6h=Delta{interval=6h, change=5, percent=5.25}, 12h=Delta{interval=12h, change=5, "
                + "percent=5.25}, 1d=Delta{interval=1d, change=10, percent=10.25}}}", transactionSummary.toString());
    }
}
