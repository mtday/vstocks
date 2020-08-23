package vstocks.rest.task.system;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.system.ActiveTransactionCountService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class ActiveTransactionCountUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

        new ActiveTransactionCountUpdateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(ActiveTransactionCountUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        ActiveTransactionCountService activeTransactionCountService = mock(ActiveTransactionCountService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getActiveTransactionCountService()).thenReturn(activeTransactionCountService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new ActiveTransactionCountUpdateTask(environment).run();

        verify(activeTransactionCountService, times(1)).generate();
        verify(activeTransactionCountService, times(1)).ageOff(any(Instant.class));
    }
}
