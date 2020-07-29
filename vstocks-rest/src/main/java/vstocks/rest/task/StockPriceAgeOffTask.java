package vstocks.rest.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.service.ServiceFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static vstocks.config.Config.DATA_HISTORY_DAYS;

public class StockPriceAgeOffTask implements BaseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockPriceAgeOffTask.class);

    private final ServiceFactory serviceFactory;

    public StockPriceAgeOffTask(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
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
        int days = DATA_HISTORY_DAYS.getInt();
        Instant cutoff = Instant.now().truncatedTo(ChronoUnit.DAYS).minus(days, ChronoUnit.DAYS);
        LOGGER.info("Aging off data older than {} days ({})", days, cutoff);
        serviceFactory.getStockPriceService().ageOff(cutoff);
    }
}
