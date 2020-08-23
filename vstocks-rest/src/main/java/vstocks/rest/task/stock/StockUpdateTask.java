package vstocks.rest.task.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.db.ServiceFactory;
import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.rest.Environment;
import vstocks.rest.task.HourlyTask;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.remote.RemoteStockService;
import vstocks.service.remote.RemoteStockServiceFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

public class StockUpdateTask extends HourlyTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(StockUpdateTask.class);

    private final Environment environment;
    private final ExecutorService executorService;

    public StockUpdateTask(Environment environment, ExecutorService executorService) {
        this.environment = environment;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        try {
            LOGGER.info("Updating all stock prices");
            RemoteStockServiceFactory remoteStockServiceFactory = environment.getRemoteStockServiceFactory();
            ServiceFactory serviceFactory = environment.getServiceFactory();

            Consumer<PricedStock> updateConsumer = pricedStock -> {
                LOGGER.info("Updating: {}", pricedStock);
                serviceFactory.getStockService().update(pricedStock.asStock());
                serviceFactory.getStockPriceService().add(pricedStock.asStockPrice());
            };
            for (Market market : Market.values()) {
                RemoteStockService remoteStockService = remoteStockServiceFactory.getForMarket(market);
                try (StockUpdateRunnable runnable = remoteStockService.getUpdateRunnable(executorService, updateConsumer)) {
                    executorService.submit(runnable);
                    serviceFactory.getOwnedStockService().consumeForMarket(market, runnable, emptyList());
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
