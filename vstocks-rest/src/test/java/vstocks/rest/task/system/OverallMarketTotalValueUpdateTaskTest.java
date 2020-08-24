package vstocks.rest.task.system;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.system.OverallMarketTotalValueService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class OverallMarketTotalValueUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

        new OverallMarketTotalValueUpdateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(OverallMarketTotalValueUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        OverallMarketTotalValueService overallMarketTotalValueService = mock(OverallMarketTotalValueService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getOverallMarketTotalValueService()).thenReturn(overallMarketTotalValueService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new OverallMarketTotalValueUpdateTask(environment).run();

        verify(overallMarketTotalValueService, times(1)).generate();
        verify(overallMarketTotalValueService, times(1)).ageOff(any(Instant.class));
    }
}
