package vstocks.rest.task.portfolio;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.portfolio.TotalRankService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class TotalRankUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);

        new TotalRankUpdateTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(TotalRankUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        TotalRankService totalRankService = mock(TotalRankService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getTotalRankService()).thenReturn(totalRankService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new TotalRankUpdateTask(environment).run();

        verify(totalRankService, times(1)).generate();
        verify(totalRankService, times(1)).ageOff(any(Instant.class));
    }
}
