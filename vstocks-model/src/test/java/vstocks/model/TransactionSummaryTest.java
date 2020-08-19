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

public class TransactionSummaryTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionSummary transactionSummary = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);

        assertEquals(now, transactionSummary.getTimestamp());
        assertEquals(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)), transactionSummary.getTransactions());
        assertEquals(1244, transactionSummary.getTotal());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionSummary transactionSummary1 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        TransactionSummary transactionSummary2 = new TransactionSummary()
                .setTimestamp(now)
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1244L);
        assertEquals(transactionSummary1, transactionSummary2);
    }

    @Test
    public void testHashCode() {
        TransactionSummary transactionSummary = new TransactionSummary()
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setTransactions(Map.of(TWITTER, 10L, YOUTUBE, 20L))
                .setTotal(1244L);
        assertNotEquals(0, new TransactionSummary().hashCode()); // enums make the value inconsistent
        assertNotEquals(0, transactionSummary.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        TransactionSummary transactionSummary = new TransactionSummary()
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setTransactions(new TreeMap<>(Map.of(TWITTER, 10L, YOUTUBE, 20L)))
                .setTotal(1264L);
        assertEquals("TransactionSummary{timestamp=2020-08-10T01:02:03Z, transactions={Twitter=10, YouTube=20}, "
                + "total=1264}", transactionSummary.toString());
    }
}
