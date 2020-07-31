package vstocks.rest.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.service.db.DatabaseServiceFactory;
import vstocks.service.remote.RemoteStockService;
import vstocks.service.remote.RemoteStockServiceFactory;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

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
        databaseServiceFactory.getStockService().consume(stock ->
                executorService.submit(new StockPriceUpdater(remoteStockServiceFactory, databaseServiceFactory, stock)));
    }

    private static class StockPriceUpdater implements Runnable {
        private final RemoteStockServiceFactory remoteStockServiceFactory;
        private final DatabaseServiceFactory databaseServiceFactory;
        private final Stock stock;

        public StockPriceUpdater(RemoteStockServiceFactory remoteStockServiceFactory,
                                 DatabaseServiceFactory databaseServiceFactory,
                                 Stock stock) {
            this.remoteStockServiceFactory = remoteStockServiceFactory;
            this.databaseServiceFactory = databaseServiceFactory;
            this.stock = stock;
        }

        @Override
        public void run() {
            try {
                RemoteStockService remoteStockService = remoteStockServiceFactory.getForMarket(stock.getMarket());

                StockPrice stockPrice = new StockPrice()
                        .setId(UUID.randomUUID().toString())
                        .setMarket(stock.getMarket())
                        .setStockId(stock.getId())
                        .setTimestamp(Instant.now())
                        .setPrice(0);
                remoteStockService.update(stock, stockPrice);

                databaseServiceFactory.getStockService().update(stock);
                databaseServiceFactory.getStockPriceService().add(stockPrice);

                LOGGER.info("Stock {}/{} updated price {}", stock.getMarket(), stock.getSymbol(), stockPrice.getPrice());
            } catch (Throwable failed) {
                LOGGER.error("Failed to update price for stock {}", stock, failed);
            }
        }
    }
}
