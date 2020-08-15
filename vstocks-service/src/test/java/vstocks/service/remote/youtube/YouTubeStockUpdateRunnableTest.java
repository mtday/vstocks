package vstocks.service.remote.youtube;

import org.junit.Test;
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
import static vstocks.model.Market.YOUTUBE;

public class YouTubeStockUpdateRunnableTest {
    private Stock getStock(int id) {
        return new Stock().setMarket(YOUTUBE).setSymbol("channel" + id).setName("Channel " + id);
    }

    private List<Integer> testBatch(int channelCount) throws ExecutionException, InterruptedException {
        YouTubeService youTubeService = mock(YouTubeService.class);
        ExecutorService mockExecutor = mock(ExecutorService.class);
        List<YouTubeStockUpdateBatchRunnable> runnables = new ArrayList<>();
        when(mockExecutor.submit(any(Runnable.class))).thenAnswer(i -> {
            runnables.add(i.getArgument(0));
            return null;
        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<?> future;
        try (YouTubeStockUpdateRunnable runnable = new YouTubeStockUpdateRunnable(youTubeService, mockExecutor, null)) {
            Stream.iterate(1, i -> i + 1)
                    .limit(channelCount)
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
                .map(YouTubeStockUpdateBatchRunnable::getStocks)
                .map(Collection::size)
                .collect(toList());
    }

    @Test
    public void testSingleBatch() throws ExecutionException, InterruptedException {
        assertEquals("[10]", testBatch(10).toString());
    }

    @Test
    public void testExactBatch() throws ExecutionException, InterruptedException {
        assertEquals("[20]", testBatch(20).toString());
    }

    @Test
    public void testMultipleBatches() throws ExecutionException, InterruptedException {
        assertEquals("[20, 20, 10]", testBatch(50).toString());
    }
}
