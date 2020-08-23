package vstocks.rest.task.portfolio;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.portfolio.MarketTotalRankService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class MarketTotalRankUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

        new MarketTotalRankUpdateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(MarketTotalRankUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        MarketTotalRankService marketTotalRankService = mock(MarketTotalRankService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getMarketTotalRankService()).thenReturn(marketTotalRankService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new MarketTotalRankUpdateTask(environment).run();

        verify(marketTotalRankService, times(1)).generate();
        verify(marketTotalRankService, times(1)).ageOff(any(Instant.class));
    }
}
