package vstocks.service.remote.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelLocalization;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.ChannelStatistics;
import org.junit.Test;
import vstocks.model.PricedStock;
import vstocks.model.Stock;
import vstocks.service.StockUpdateRunnable;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.service.remote.youtube.YouTubeRemoteStockService.getPrice;

public class YouTubeRemoteStockServiceTest {
    private final Function<Integer, Channel> getChannelWithSubscribers = subscribers -> {
        ChannelStatistics channelStatistics = new ChannelStatistics()
                .setSubscriberCount(new BigInteger(String.valueOf(subscribers)));
        return new Channel().setStatistics(channelStatistics);
    };

    @Test
    public void testGetPrice() {
        /*
        Stream.iterate(0, i -> i == 0 ? 5 : (("" + i).contains("5") ? i * 2 : i * 5)).limit(17).forEach(i ->
                LOGGER.info("Price: {} => {}", i, getPrice(getChannelWithSubscribers.apply(i))));
         */

        assertEquals(1, getPrice(getChannelWithSubscribers.apply(0)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(5)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(10)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(50)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(100)));
        assertEquals(3, getPrice(getChannelWithSubscribers.apply(500)));
        assertEquals(5, getPrice(getChannelWithSubscribers.apply(1_000)));
        assertEquals(25, getPrice(getChannelWithSubscribers.apply(5_000)));
        assertEquals(50, getPrice(getChannelWithSubscribers.apply(10_000)));
        assertEquals(239, getPrice(getChannelWithSubscribers.apply(50_000)));
        assertEquals(455, getPrice(getChannelWithSubscribers.apply(100_000)));
        assertEquals(1667, getPrice(getChannelWithSubscribers.apply(500_000)));
        assertEquals(2501, getPrice(getChannelWithSubscribers.apply(1_000_000)));
        assertEquals(4167, getPrice(getChannelWithSubscribers.apply(5_000_000)));
        assertEquals(4546, getPrice(getChannelWithSubscribers.apply(10_000_000)));
        assertEquals(4902, getPrice(getChannelWithSubscribers.apply(50_000_000)));
        assertEquals(4951, getPrice(getChannelWithSubscribers.apply(100_000_000)));
    }

    private static Channel getChannel(String id, int subscribers, String title) {
        ChannelSnippet channelSnippet = new ChannelSnippet()
                .setLocalized(new ChannelLocalization().setTitle(title));
        ChannelStatistics channelStatistics = new ChannelStatistics()
                .setSubscriberCount(new BigInteger(String.valueOf(subscribers)));
        return new Channel().setId(id).setSnippet(channelSnippet).setStatistics(channelStatistics);
    }

    @Test
    public void testGetUpdateRunnable() throws ExecutionException, InterruptedException, IOException {
        YouTubeService youTubeService = mock(YouTubeService.class);
        when(youTubeService.getChannels(any()))
                .thenReturn(asList(getChannel("1", 50_000, "Channel 1"), getChannel("2", 100_000, "Channel 2")));

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<PricedStock> entries = new ArrayList<>();

        YouTubeRemoteStockService youTubeRemoteStockService = new YouTubeRemoteStockService(youTubeService);
        Future<?> future;
        try (StockUpdateRunnable runnable = youTubeRemoteStockService.getUpdateRunnable(executorService, entries::add)) {
            runnable.accept(new Stock().setMarket(YOUTUBE).setSymbol("1").setName("Channel 1"));
            runnable.accept(new Stock().setMarket(YOUTUBE).setSymbol("2").setName("Channel 2"));
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

        assertEquals(2, entries.size());

        assertEquals("1", entries.get(0).getSymbol());
        assertEquals("Channel 1", entries.get(0).getName());
        assertEquals(239, entries.get(0).getPrice());

        assertEquals("2", entries.get(1).getSymbol());
        assertEquals("Channel 2", entries.get(1).getName());
        assertEquals(455, entries.get(1).getPrice());
    }

    @Test
    public void testSearch() {
        Channel channel1 = getChannel("1", 50_000, "Channel 1");
        Channel channel2 = getChannel("2", 100_000, "Channel 2");

        YouTubeService youTubeService = mock(YouTubeService.class);
        when(youTubeService.search(eq("user"), eq(20))).thenReturn(asList(channel1, channel2));

        YouTubeRemoteStockService youTubeRemoteStockService = new YouTubeRemoteStockService(youTubeService);
        List<PricedStock> stocks = youTubeRemoteStockService.search("user", 20);

        assertEquals(2, stocks.size());
        assertEquals(YOUTUBE, stocks.get(0).getMarket());
        assertEquals("1", stocks.get(0).getSymbol());
        assertEquals("Channel 1", stocks.get(0).getName());
        assertEquals(239, stocks.get(0).getPrice());
        assertEquals(YOUTUBE, stocks.get(1).getMarket());
        assertEquals("2", stocks.get(1).getSymbol());
        assertEquals("Channel 2", stocks.get(1).getName());
        assertEquals(455, stocks.get(1).getPrice());
    }
}
