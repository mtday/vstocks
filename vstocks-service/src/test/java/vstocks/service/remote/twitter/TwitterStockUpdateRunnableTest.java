package vstocks.service.remote.twitter;

import org.junit.Test;
import twitter4j.Twitter;
import vstocks.model.Stock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.TWITTER;

public class TwitterStockUpdateRunnableTest {
    private Stock getStock(int id) {
        return new Stock().setMarket(TWITTER).setSymbol("user" + id).setName("User " + id);
    }

    private List<Integer> testBatch(int userCount) throws ExecutionException, InterruptedException {
        Twitter twitter = mock(Twitter.class);
        ExecutorService mockExecutor = mock(ExecutorService.class);
        List<TwitterStockUpdateBatchRunnable> runnables = new ArrayList<>();
        when(mockExecutor.submit(any(Runnable.class))).thenAnswer(i -> {
            runnables.add(i.getArgument(0));
            return null;
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<?> future;
        try (TwitterStockUpdateRunnable runnable = new TwitterStockUpdateRunnable(twitter, mockExecutor, null)) {
            Stream.iterate(1, i -> i + 1)
                    .limit(userCount)
                    .map(this::getStock)
                    .forEach(runnable);
            future = executorService.submit(runnable);
        }
        future.get();

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException interrupted) {
            throw new RuntimeException(interrupted);
        }
        return runnables.stream()
                .map(TwitterStockUpdateBatchRunnable::getStocks)
                .map(Collection::size)
                .collect(toList());
    }

    @Test
    public void testSingleBatch() throws ExecutionException, InterruptedException {
        assertEquals("[10]", testBatch(10).toString());
    }

    @Test
    public void testExactBatch() throws ExecutionException, InterruptedException {
        assertEquals("[100]", testBatch(100).toString());
    }

    @Test
    public void testMultipleBatches() throws ExecutionException, InterruptedException {
        assertEquals("[100, 100, 20]", testBatch(220).toString());
    }
}
