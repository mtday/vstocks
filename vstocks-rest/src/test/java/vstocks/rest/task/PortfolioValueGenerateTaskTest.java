package vstocks.rest.task;

import org.junit.Test;
import org.mockito.stubbing.Answer;
import vstocks.db.DBFactory;
import vstocks.db.PortfolioValueDB;
import vstocks.db.PortfolioValueRankDB;
import vstocks.db.PortfolioValueSummaryDB;
import vstocks.model.Market;
import vstocks.model.PortfolioValue;
import vstocks.model.PortfolioValueRank;
import vstocks.model.PortfolioValueSummary;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.time.temporal.ChronoUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PortfolioValueGenerateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        new PortfolioValueGenerateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(PortfolioValueGenerateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRunNoData() {
        PortfolioValueDB portfolioValueDB = mock(PortfolioValueDB.class);
        when(portfolioValueDB.generateAll(any())).thenReturn(0);
        List<PortfolioValue> valuesAdded = new ArrayList<>();
        when(portfolioValueDB.addAll(any())).then((Answer<Integer>) invocation -> {
            List<PortfolioValue> valuesToAdd = invocation.getArgument(0);
            valuesAdded.addAll(valuesToAdd);
            return valuesToAdd.size();
        });

        PortfolioValueRankDB portfolioValueRankDB = mock(PortfolioValueRankDB.class);
        List<PortfolioValueRank> ranksAdded = new ArrayList<>();
        when(portfolioValueRankDB.addAll(any())).then((Answer<Integer>) invocation -> {
            List<PortfolioValueRank> ranksToAdd = invocation.getArgument(0);
            ranksAdded.addAll(ranksToAdd);
            return ranksToAdd.size();
        });

        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 0L));
        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary()
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setCredits(0)
                .setMarketValues(marketValues)
                .setTotal(0);
        PortfolioValueSummaryDB portfolioValueSummaryDB = mock(PortfolioValueSummaryDB.class);
        when(portfolioValueSummaryDB.generate()).thenReturn(portfolioValueSummary);
        List<PortfolioValueSummary> summariesAdded = new ArrayList<>();
        when(portfolioValueSummaryDB.add(any())).then((Answer<Integer>) invocation -> {
            PortfolioValueSummary summary = invocation.getArgument(0);
            summariesAdded.add(summary);
            return 1;
        });

        DBFactory dbFactory = mock(DBFactory.class);
        when(dbFactory.getPortfolioValueDB()).thenReturn(portfolioValueDB);
        when(dbFactory.getPortfolioValueRankDB()).thenReturn(portfolioValueRankDB);
        when(dbFactory.getPortfolioValueSummaryDB()).thenReturn(portfolioValueSummaryDB);

        Environment environment = mock(Environment.class);
        when(environment.getDBFactory()).thenReturn(dbFactory);

        new PortfolioValueGenerateTask(environment).run();

        assertTrue(valuesAdded.isEmpty());
        assertTrue(ranksAdded.isEmpty());
        assertEquals(1, summariesAdded.size());
        assertEquals(portfolioValueSummary, summariesAdded.iterator().next());
    }

    @Test
    public void testRunWithData() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));
        PortfolioValue portfolioValue1 = new PortfolioValue()
                .setUserId("user1")
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setCredits(10)
                .setMarketValues(marketValues)
                .setTotal(60);
        PortfolioValue portfolioValue2 = new PortfolioValue()
                .setUserId("user2")
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setCredits(8)
                .setMarketValues(marketValues)
                .setTotal(58);
        PortfolioValue portfolioValue3 = new PortfolioValue()
                .setUserId("user3")
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setCredits(6)
                .setMarketValues(marketValues)
                .setTotal(56);
        PortfolioValueDB portfolioValueDB = mock(PortfolioValueDB.class);
        when(portfolioValueDB.generateAll(any())).then((Answer<Integer>) invocation -> {
            Consumer<PortfolioValue> consumer = invocation.getArgument(0);
            consumer.accept(portfolioValue1);
            consumer.accept(portfolioValue2);
            consumer.accept(portfolioValue3);
            return 3;
        });
        List<PortfolioValue> valuesAdded = new ArrayList<>();
        when(portfolioValueDB.addAll(any())).then((Answer<Integer>) invocation -> {
            List<PortfolioValue> valuesToAdd = invocation.getArgument(0);
            valuesAdded.addAll(valuesToAdd);
            return valuesToAdd.size();
        });

        PortfolioValueRankDB portfolioValueRankDB = mock(PortfolioValueRankDB.class);
        List<PortfolioValueRank> ranksAdded = new ArrayList<>();
        when(portfolioValueRankDB.addAll(any())).then((Answer<Integer>) invocation -> {
            List<PortfolioValueRank> ranksToAdd = invocation.getArgument(0);
            ranksAdded.addAll(ranksToAdd);
            return ranksToAdd.size();
        });

        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary()
                .setTimestamp(Instant.now().truncatedTo(SECONDS))
                .setCredits(10)
                .setMarketValues(marketValues)
                .setTotal(60);
        PortfolioValueSummaryDB portfolioValueSummaryDB = mock(PortfolioValueSummaryDB.class);
        when(portfolioValueSummaryDB.generate()).thenReturn(portfolioValueSummary);
        List<PortfolioValueSummary> summariesAdded = new ArrayList<>();
        when(portfolioValueSummaryDB.add(any())).then((Answer<Integer>) invocation -> {
            PortfolioValueSummary summary = invocation.getArgument(0);
            summariesAdded.add(summary);
            return 1;
        });

        DBFactory dbFactory = mock(DBFactory.class);
        when(dbFactory.getPortfolioValueDB()).thenReturn(portfolioValueDB);
        when(dbFactory.getPortfolioValueRankDB()).thenReturn(portfolioValueRankDB);
        when(dbFactory.getPortfolioValueSummaryDB()).thenReturn(portfolioValueSummaryDB);

        Environment environment = mock(Environment.class);
        when(environment.getDBFactory()).thenReturn(dbFactory);

        new PortfolioValueGenerateTask(environment, 2).run();

        assertEquals(3, valuesAdded.size());
        assertEquals(portfolioValue1, valuesAdded.get(0));
        assertEquals(portfolioValue2, valuesAdded.get(1));
        assertEquals(portfolioValue3, valuesAdded.get(2));

        PortfolioValueRank portfolioValueRank1 = new PortfolioValueRank()
                .setUserId("user1")
                .setTimestamp(portfolioValue1.getTimestamp())
                .setRank(1);
        PortfolioValueRank portfolioValueRank2 = new PortfolioValueRank()
                .setUserId("user2")
                .setTimestamp(portfolioValue2.getTimestamp())
                .setRank(2);
        PortfolioValueRank portfolioValueRank3 = new PortfolioValueRank()
                .setUserId("user3")
                .setTimestamp(portfolioValue3.getTimestamp())
                .setRank(3);

        assertEquals(3, ranksAdded.size());
        assertEquals(portfolioValueRank1, ranksAdded.get(0));
        assertEquals(portfolioValueRank2, ranksAdded.get(1));
        assertEquals(portfolioValueRank3, ranksAdded.get(2));

        assertEquals(1, summariesAdded.size());
        assertEquals(portfolioValueSummary, summariesAdded.iterator().next());
    }
}
