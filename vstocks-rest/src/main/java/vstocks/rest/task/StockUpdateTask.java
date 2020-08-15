package vstocks.rest.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.db.DBFactory;
import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.rest.Environment;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.remote.RemoteStockService;
import vstocks.service.remote.RemoteStockServiceFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import static java.time.temporal.ChronoField.*;
import static java.util.Collections.emptySet;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class StockUpdateTask implements Task {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockUpdateTask.class);

    private final Environment environment;
    private final ExecutorService executorService;

    public StockUpdateTask(Environment environment, ExecutorService executorService) {
        this.environment = environment;
        this.executorService = executorService;
    }

    @Override
    public void schedule(ScheduledExecutorService scheduledExecutorService) {
        // Determine how long to delay so that our scheduled task runs at approximately each 10 minute mark.
        LocalDateTime now = LocalDateTime.now();
        int minute = now.get(MINUTE_OF_HOUR) % 10;
        int second = now.get(SECOND_OF_MINUTE);
        int millis = now.get(MILLI_OF_SECOND);
        long delayMinutes = (9 - minute) * 60000;
        long delaySeconds = (second > 0 ? 59 - second : 59) * 1000;
        long delayMillis = millis > 0 ? 1000 - millis : 1000;
        long delay = delayMinutes + delaySeconds + delayMillis;

        //scheduledExecutorService.scheduleAtFixedRate(this, delay, MINUTES.toMillis(10), MILLISECONDS);
        // More frequent schedule for testing (once per minute):
        scheduledExecutorService.scheduleAtFixedRate(this, delay % MINUTES.toMillis(1), MINUTES.toMillis(1), MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Updating all stock prices");
            RemoteStockServiceFactory remoteStockServiceFactory = environment.getRemoteStockServiceFactory();
            DBFactory dbFactory = environment.getDBFactory();

            Consumer<PricedStock> updateConsumer = pricedStock -> {
                LOGGER.info("Updating: {}/{} ({}) => {}", pricedStock.getMarket(), pricedStock.getSymbol(),
                        pricedStock.getName(), pricedStock.getPrice());
                dbFactory.getStockDB().update(pricedStock.asStock());
                dbFactory.getStockPriceDB().add(pricedStock.asStockPrice());
            };
            for (Market market : Market.values()) {
                RemoteStockService remoteStockService = remoteStockServiceFactory.getForMarket(market);
                try (StockUpdateRunnable runnable = remoteStockService.getUpdateRunnable(executorService, updateConsumer)) {
                    executorService.submit(runnable);
                    dbFactory.getOwnedStockDB().consumeForMarket(market, runnable, emptySet());
                } catch (IOException e) {
                    LOGGER.error("Failed to close stock update runnable", e);
                }
            }
            LOGGER.info("Done updating stock prices");
        } catch (Throwable e) {
            LOGGER.error("Unexpected error", e);
        }
    }
}
