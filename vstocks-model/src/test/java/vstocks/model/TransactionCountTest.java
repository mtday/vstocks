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

public class TransactionCountTest {
    @Test
    public void testGettersAndSetters() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        Map<DeltaInterval, Delta> deltas = new TreeMap<>(Map.of(
                HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
        ));
        TransactionCount transactionCount = new TransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setTransactions(1234L)
                .setDeltas(deltas);

        assertEquals(TWITTER, transactionCount.getMarket());
        assertEquals(now, transactionCount.getTimestamp());
        assertEquals(1234, transactionCount.getTransactions());
        assertEquals(deltas, transactionCount.getDeltas());
    }

    @Test
    public void testEquals() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionCount transactionCount1 = new TransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setTransactions(1234L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        TransactionCount transactionCount2 = new TransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setTransactions(1234L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals(transactionCount1, transactionCount2);
    }

    @Test
    public void testHashCode() {
        TransactionCount transactionCount = new TransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(Instant.parse("2020-08-10T01:02:03.00Z"))
                .setTransactions(1234L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertNotEquals(0, new TransactionCount().hashCode()); // enums make the value inconsistent
        assertNotEquals(0, transactionCount.hashCode()); // enums make the value inconsistent
    }

    @Test
    public void testToString() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionCount transactionCount = new TransactionCount()
                .setMarket(TWITTER)
                .setTimestamp(now)
                .setTransactions(1234L)
                .setDeltas(new TreeMap<>(Map.of(
                        HOUR6, new Delta().setInterval(HOUR6).setChange(5).setPercent(5.25f),
                        HOUR12, new Delta().setInterval(HOUR12).setChange(5).setPercent(5.25f),
                        DAY1, new Delta().setInterval(DAY1).setChange(10).setPercent(10.25f)
                )));
        assertEquals("TransactionCount{market=Twitter, timestamp=" + now + ", transactions=1234, deltas="
                + "{6h=Delta{interval=6h, change=5, percent=5.25}, 12h=Delta{interval=12h, change=5, percent=5.25}, "
                + "1d=Delta{interval=1d, change=10, percent=10.25}}}", transactionCount.toString());
    }
}
