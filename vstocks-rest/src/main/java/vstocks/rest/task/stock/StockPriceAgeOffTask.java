package vstocks.rest.task.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.rest.Environment;
import vstocks.rest.task.HourlyTask;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.DAYS;
import static vstocks.config.Config.DATA_HISTORY_DAYS;

public class StockPriceAgeOffTask extends HourlyTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockPriceAgeOffTask.class);

    private final Environment environment;

    public StockPriceAgeOffTask(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run() {
        int days = DATA_HISTORY_DAYS.getInt();
        Instant cutoff = Instant.now().truncatedTo(DAYS).minus(days, DAYS);
        environment.getServiceFactory().getStockPriceService().ageOff(cutoff);
        environment.getServiceFactory().getStockPriceChangeService().ageOff(cutoff);
    }
}
