package vstocks.rest.task;

import org.junit.Test;
import vstocks.db.ServiceFactory;
import vstocks.db.StockPriceService;
import vstocks.rest.Environment;

import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class StockPriceAgeOffTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        new StockPriceAgeOffTask(environment).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(StockPriceAgeOffTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() {
        StockPriceService stockPriceDB = mock(StockPriceService.class);

        ServiceFactory dbFactory = mock(ServiceFactory.class);
        when(dbFactory.getStockPriceService()).thenReturn(stockPriceDB);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(dbFactory);

        new StockPriceAgeOffTask(environment).run();

        verify(stockPriceDB, times(1)).ageOff(any(Instant.class));
    }
}
