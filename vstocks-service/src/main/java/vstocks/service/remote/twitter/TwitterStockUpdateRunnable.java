package vstocks.service.remote.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.User;
import vstocks.model.Stock;
import vstocks.service.StockUpdateRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

class TwitterStockUpdateRunnable implements StockUpdateRunnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterStockUpdateRunnable.class);

    private static final int QUEUE_SIZE = 1000;
    private static final int BATCH_SIZE = 100;

    private final Twitter twitter;
    private final ExecutorService executorService;
    private final Consumer<User> userConsumer;

    private final BlockingQueue<Stock> stocksToUpdate = new ArrayBlockingQueue<>(QUEUE_SIZE);
    private volatile boolean closed = false;

    public TwitterStockUpdateRunnable(Twitter twitter,
                                      ExecutorService executorService,
                                      Consumer<User> userConsumer) {
        this.twitter = twitter;
        this.executorService = executorService;
        this.userConsumer = userConsumer;
    }

    @Override
    public void accept(Stock stock) {
        stocksToUpdate.add(stock);
    }

    @Override
    public void run() {
        try {
            while (!closed || !stocksToUpdate.isEmpty()) {
                // Attempt to fill a batch without delaying for too long.
                List<Stock> batch = new ArrayList<>(BATCH_SIZE);
                if (stocksToUpdate.drainTo(batch, BATCH_SIZE) > 0 && batch.size() < BATCH_SIZE) {
                    boolean batchComplete = false;
                    while (!batchComplete && batch.size() < BATCH_SIZE) {
                        try {
                            Stock toUpdate = stocksToUpdate.poll(50, MILLISECONDS);
                            if (toUpdate == null) {
                                batchComplete = true;
                            } else {
                                batch.add(toUpdate);
                                if (batch.size() < BATCH_SIZE) {
                                    stocksToUpdate.drainTo(batch, BATCH_SIZE - batch.size());
                                }
                            }
                        } catch (InterruptedException e) {
                            batchComplete = true;
                        }
                    }
                }

                if (!batch.isEmpty()) {
                    executorService.submit(new TwitterStockUpdateBatchRunnable(twitter, userConsumer, batch));
                }
            }
        } catch (Throwable e) {
            LOGGER.error("Unexpected error", e);
        }
    }

    @Override
    public void close() {
        closed = true;
    }
}
