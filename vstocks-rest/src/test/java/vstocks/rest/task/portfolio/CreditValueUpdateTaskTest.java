package vstocks.rest.task.portfolio;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.portfolio.CreditValueService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class CreditValueUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

        new CreditValueUpdateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(CreditValueUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        CreditValueService creditValueService = mock(CreditValueService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getCreditValueService()).thenReturn(creditValueService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new CreditValueUpdateTask(environment).run();

        verify(creditValueService, times(1)).generate();
        verify(creditValueService, times(1)).ageOff(any(Instant.class));
    }
}
