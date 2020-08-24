package vstocks.rest.task.system;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.system.OverallMarketValueService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class OverallMarketValueUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

        new OverallMarketValueUpdateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(OverallMarketValueUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        OverallMarketValueService overallMarketValueService = mock(OverallMarketValueService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getOverallMarketValueService()).thenReturn(overallMarketValueService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new OverallMarketValueUpdateTask(environment).run();

        verify(overallMarketValueService, times(1)).generate();
        verify(overallMarketValueService, times(1)).ageOff(any(Instant.class));
    }
}
