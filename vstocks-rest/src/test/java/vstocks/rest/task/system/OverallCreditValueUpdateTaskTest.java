package vstocks.rest.task.system;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.system.OverallCreditValueService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class OverallCreditValueUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

        new OverallCreditValueUpdateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(OverallCreditValueUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        OverallCreditValueService overallCreditValueService = mock(OverallCreditValueService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getOverallCreditValueService()).thenReturn(overallCreditValueService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new OverallCreditValueUpdateTask(environment).run();

        verify(overallCreditValueService, times(1)).generate();
        verify(overallCreditValueService, times(1)).ageOff(any(Instant.class));
    }
}
