package vstocks.rest.task.portfolio;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.portfolio.CreditRankService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class CreditRankUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

        new CreditRankUpdateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(CreditRankUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        CreditRankService creditRankService = mock(CreditRankService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getCreditRankService()).thenReturn(creditRankService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new CreditRankUpdateTask(environment).run();

        verify(creditRankService, times(1)).generate();
        verify(creditRankService, times(1)).ageOff(any(Instant.class));
    }
}
