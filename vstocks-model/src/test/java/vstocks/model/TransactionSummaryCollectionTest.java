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

public class TransactionSummaryCollectionTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionSummary transactionSummary1 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 20L, YOUTUBE, 20L)))
                .setTotal(40L);
        TransactionSummary transactionSummary2 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(30L);

        List<TransactionSummary> summaries = asList(transactionSummary1, transactionSummary2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        TransactionSummaryCollection collection =
                new TransactionSummaryCollection().setSummaries(summaries).setDeltas(deltas);

        assertEquals(summaries, collection.getSummaries());
        assertEquals(deltas, collection.getDeltas());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionSummary transactionSummary1 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 20L, YOUTUBE, 20L)))
                .setTotal(40L);
        TransactionSummary transactionSummary2 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(30L);

        List<TransactionSummary> summaries = asList(transactionSummary1, transactionSummary2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        TransactionSummaryCollection collection1 =
                new TransactionSummaryCollection().setSummaries(summaries).setDeltas(deltas);
        TransactionSummaryCollection collection2 =
                new TransactionSummaryCollection().setSummaries(summaries).setDeltas(deltas);

        assertEquals(collection1, collection2);
    }

    @Test
    public void testHashCode() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionSummary transactionSummary1 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 20L, YOUTUBE, 20L)))
                .setTotal(40L);
        TransactionSummary transactionSummary2 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(30L);

        List<TransactionSummary> summaries = asList(transactionSummary1, transactionSummary2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        TransactionSummaryCollection collection =
                new TransactionSummaryCollection().setSummaries(summaries).setDeltas(deltas);

        assertNotEquals(0, new TransactionSummaryCollection().hashCode()); // enums make the value inconsistent
        assertNotEquals(0, collection.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionSummary transactionSummary1 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 20L, YOUTUBE, 20L)))
                .setTotal(40L);
        TransactionSummary transactionSummary2 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(30L);

        List<TransactionSummary> summaries = asList(transactionSummary1, transactionSummary2);

        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));

        TransactionSummaryCollection collection =
                new TransactionSummaryCollection().setSummaries(summaries).setDeltas(deltas);

        assertEquals("TransactionSummaryCollection{summaries=[" + transactionSummary1 + ", " + transactionSummary2
                        + "], deltas=" + deltas + "}", collection.toString());
    }
}
