package vstocks.rest.task;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.PortfolioValueDB;
import vstocks.db.PortfolioValueRankDB;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class PortfolioValueAgeOffTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        new PortfolioValueAgeOffTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(PortfolioValueAgeOffTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        PortfolioValueDB portfolioValueDB = mock(PortfolioValueDB.class);
        PortfolioValueRankDB portfolioValueRankDB = mock(PortfolioValueRankDB.class);

        ServiceFactory dbFactory = mock(ServiceFactory.class);
        when(dbFactory.getPortfolioValueDB()).thenReturn(portfolioValueDB);
        when(dbFactory.getPortfolioValueRankDB()).thenReturn(portfolioValueRankDB);

        Environment environment = mock(Environment.class);
        when(environment.getDBFactory()).thenReturn(dbFactory);

        new PortfolioValueAgeOffTask(environment).run();

        verify(portfolioValueDB, times(1)).ageOff(any(Instant.class));
        verify(portfolioValueRankDB, times(1)).ageOff(any(Instant.class));
    }
}
