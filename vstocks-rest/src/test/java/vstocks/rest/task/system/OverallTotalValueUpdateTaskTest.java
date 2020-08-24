package vstocks.rest.task.system;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.system.OverallTotalValueService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class OverallTotalValueUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

        new OverallTotalValueUpdateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(OverallTotalValueUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        OverallTotalValueService overallTotalValueService = mock(OverallTotalValueService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getOverallTotalValueService()).thenReturn(overallTotalValueService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new OverallTotalValueUpdateTask(environment).run();

        verify(overallTotalValueService, times(1)).generate();
        verify(overallTotalValueService, times(1)).ageOff(any(Instant.class));
    }
}
