package vstocks.rest.task;

import org.junit.Test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class MemoryUsageLoggingTaskTest {
    @Test
    public void testSchedule() {
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        new MemoryUsageLoggingTask().schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(MemoryUsageLoggingTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        new MemoryUsageLoggingTask().run();
    }
}
