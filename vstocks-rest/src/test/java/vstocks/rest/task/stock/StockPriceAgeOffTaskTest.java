package vstocks.rest.task.stock;

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
        StockPriceService stockPriceService = mock(StockPriceService.class);

        ServiceFactory serviceFactory = mock(ServiceFactory.class);
        when(serviceFactory.getStockPriceService()).thenReturn(stockPriceService);

        Environment environment = mock(Environment.class);
        when(environment.getServiceFactory()).thenReturn(serviceFactory);

        new StockPriceAgeOffTask(environment).run();

        verify(stockPriceService, times(1)).ageOff(any(Instant.class));
    }
}
