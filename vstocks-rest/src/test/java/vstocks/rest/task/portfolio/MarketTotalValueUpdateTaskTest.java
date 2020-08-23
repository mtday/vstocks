package vstocks.rest.task.portfolio;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.portfolio.MarketTotalValueService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class MarketTotalValueUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

        new MarketTotalValueUpdateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(MarketTotalValueUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        MarketTotalValueService marketTotalValueService = mock(MarketTotalValueService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getMarketTotalValueService()).thenReturn(marketTotalValueService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new MarketTotalValueUpdateTask(environment).run();

        verify(marketTotalValueService, times(1)).generate();
        verify(marketTotalValueService, times(1)).ageOff(any(Instant.class));
    }
}
