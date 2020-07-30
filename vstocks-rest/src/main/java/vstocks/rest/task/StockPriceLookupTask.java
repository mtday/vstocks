package vstocks.rest.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.service.db.DatabaseServiceFactory;
import vstocks.service.db.StockPriceService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class StockPriceLookupTask implements BaseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockPriceLookupTask.class);

    private final DatabaseServiceFactory databaseServiceFactory;

    public StockPriceLookupTask(DatabaseServiceFactory databaseServiceFactory) {
        this.databaseServiceFactory = databaseServiceFactory;
    }

    @Override
    public void schedule(ScheduledExecutorService scheduledExecutorService) {
        // Determine how long to delay so that our scheduled task runs at approximately each 10 minute mark.
        LocalDateTime now = LocalDateTime.now();
        int minute = now.get(ChronoField.MINUTE_OF_HOUR) % 10;
        int second = now.get(ChronoField.SECOND_OF_MINUTE);
        int millis = now.get(ChronoField.MILLI_OF_SECOND);
        long delayMinutes = (9 - minute) * 60000;
        long delaySeconds = (second > 0 ? 59 - second : 59) * 1000;
        long delayMillis  = millis > 0 ? 1000 - millis : 1000;
        long delay = delayMinutes + delaySeconds + delayMillis;

        scheduledExecutorService.scheduleAtFixedRate(this, delay, MINUTES.toMillis(10), MILLISECONDS);
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();

        StockPriceService stockPriceService = databaseServiceFactory.getStockPriceService();
        databaseServiceFactory.getStockService().consume(stock -> {
            LOGGER.debug("Looking up price for stock {}/{}", stock.getMarketId(), stock.getSymbol());
        });

        long stop = System.currentTimeMillis();
        LOGGER.info("Stock Price lookup task took: {}", Duration.of(stop - start, ChronoUnit.MILLIS));
    }
}
