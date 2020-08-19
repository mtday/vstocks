package vstocks.rest.task;

import org.junit.Test;
import org.mockito.stubbing.Answer;
import vstocks.db.DBFactory;
import vstocks.db.TransactionSummaryDB;
import vstocks.model.Market;
import vstocks.model.TransactionSummary;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TransactionSummaryGenerateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        new TransactionSummaryGenerateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(TransactionSummaryGenerateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        Map<Market, Long> transactions = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> transactions.put(market, 10L));

        Instant now = Instant.now().truncatedTo(SECONDS);
        TransactionSummary transactionSummary = new TransactionSummary().setTimestamp(now).setTransactions(transactions).setTotal(50);

        TransactionSummaryDB transactionSummaryDB = mock(TransactionSummaryDB.class);
        when(transactionSummaryDB.generate()).thenReturn(transactionSummary);

        List<TransactionSummary> transactionSummariesAdded = new ArrayList<>();
        when(transactionSummaryDB.add(any())).then((Answer<Integer>) invocation -> {
            transactionSummariesAdded.add(invocation.getArgument(0));
            return 1;
        });

        DBFactory dbFactory = mock(DBFactory.class);
        when(dbFactory.getTransactionSummaryDB()).thenReturn(transactionSummaryDB);

        Environment environment = mock(Environment.class);
        when(environment.getDBFactory()).thenReturn(dbFactory);

        new TransactionSummaryGenerateTask(environment).run();

        assertEquals(1, transactionSummariesAdded.size());
        assertEquals(transactionSummary, transactionSummariesAdded.iterator().next());
    }
}
