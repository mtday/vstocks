package vstocks.rest.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.model.Market;
import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.db.DatabaseServiceFactory;
import vstocks.service.remote.RemoteStockService;
import vstocks.service.remote.RemoteStockServiceFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

public class StockUpdateTask implements BaseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockUpdateTask.class);

    private final RemoteStockServiceFactory remoteStockServiceFactory;
    private final DatabaseServiceFactory databaseServiceFactory;
    private final ExecutorService executorService;

    public StockUpdateTask(RemoteStockServiceFactory remoteStockServiceFactory,
                           DatabaseServiceFactory databaseServiceFactory,
                           ExecutorService executorService) {
        this.remoteStockServiceFactory = remoteStockServiceFactory;
        this.databaseServiceFactory = databaseServiceFactory;
        this.executorService = executorService;
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

        //scheduledExecutorService.scheduleAtFixedRate(this, delay, MINUTES.toMillis(10), MILLISECONDS);
        scheduledExecutorService.scheduleAtFixedRate(this, delay % MINUTES.toMillis(1), MINUTES.toMillis(1), MILLISECONDS);
    }

    @Override
    public void run() {
        LOGGER.info("Updating all stock prices");
        for (Market market : Market.values()) {
            RemoteStockService remoteStockService = remoteStockServiceFactory.getForMarket(market);
            Consumer<Entry<Stock, StockPrice>> updateConsumer = entry -> {
                Stock stock = entry.getKey();
                StockPrice stockPrice = entry.getValue();
                LOGGER.info("Stock {}/{} updated price {}", stock.getMarket(), stock.getSymbol(), stockPrice.getPrice());
                databaseServiceFactory.getStockService().update(stock);
                databaseServiceFactory.getStockPriceService().add(stockPrice);
            };
            try (StockUpdateRunnable runnable = remoteStockService.getUpdateRunnable(executorService, updateConsumer)) {
                executorService.submit(runnable);
                databaseServiceFactory.getStockService().consumeForMarket(market, runnable);
            } catch (IOException e) {
                LOGGER.error("Failed to close stock update runnable", e);
            }
        }
    }
}
