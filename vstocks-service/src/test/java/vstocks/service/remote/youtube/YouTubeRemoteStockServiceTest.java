package vstocks.service.remote.youtube;

import com.google.api.services.youtube.model.*;
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
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.service.remote.youtube.YouTubeRemoteStockService.getPrice;
import static vstocks.service.remote.youtube.YouTubeRemoteStockService.toPricedStock;

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
                System.out.printf("Price: %d => %d\n", i, getPrice(getChannelWithSubscribers.apply(i))));
         */

        assertEquals(1, getPrice(getChannelWithSubscribers.apply(0)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(5)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(10)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(50)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(100)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(500)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(1_000)));
        assertEquals(1, getPrice(getChannelWithSubscribers.apply(5_000)));
        assertEquals(2, getPrice(getChannelWithSubscribers.apply(10_000)));
        assertEquals(6, getPrice(getChannelWithSubscribers.apply(50_000)));
        assertEquals(12, getPrice(getChannelWithSubscribers.apply(100_000)));
        assertEquals(60, getPrice(getChannelWithSubscribers.apply(500_000)));
        assertEquals(120, getPrice(getChannelWithSubscribers.apply(1_000_000)));
        assertEquals(598, getPrice(getChannelWithSubscribers.apply(5_000_000)));
        assertEquals(1178, getPrice(getChannelWithSubscribers.apply(10_000_000)));
        assertEquals(4169, getPrice(getChannelWithSubscribers.apply(50_000_000)));
        assertEquals(4919, getPrice(getChannelWithSubscribers.apply(100_000_000)));
    }

    private static Channel getChannel(String id, int subscribers, String title, String link) {
        ThumbnailDetails thumbnailDetails = new ThumbnailDetails()
                .setDefault(new Thumbnail().setUrl(link));
        ChannelSnippet channelSnippet = new ChannelSnippet()
                .setLocalized(new ChannelLocalization().setTitle(title))
                .setThumbnails(thumbnailDetails);
        ChannelStatistics channelStatistics = new ChannelStatistics()
                .setSubscriberCount(new BigInteger(String.valueOf(subscribers)));
        return new Channel().setId(id).setSnippet(channelSnippet).setStatistics(channelStatistics);
    }

    @Test
    public void testToPricedStock() {
        PricedStock pricedStock = toPricedStock(getChannel("channel", 50_000, "title", "link"));

        assertEquals(YOUTUBE, pricedStock.getMarket());
        assertEquals("channel", pricedStock.getSymbol());
        assertEquals("title", pricedStock.getName());
        assertEquals("link", pricedStock.getProfileImage());
        assertNotNull(pricedStock.getTimestamp());
        assertEquals(6, pricedStock.getPrice());
    }

    @Test
    public void testToPricedStockNullUrl() {
        PricedStock pricedStock = toPricedStock(getChannel("channel", 50_000, "title", null));

        assertEquals(YOUTUBE, pricedStock.getMarket());
        assertEquals("channel", pricedStock.getSymbol());
        assertEquals("title", pricedStock.getName());
        assertNull(pricedStock.getProfileImage());
        assertNotNull(pricedStock.getTimestamp());
        assertEquals(6, pricedStock.getPrice());
    }

    @Test
    public void testToPricedStockNullThumbnailDetails() {
        Channel channel = getChannel("channel", 50_000, "title", null);
        channel.getSnippet().setThumbnails(null);
        PricedStock pricedStock = toPricedStock(channel);

        assertEquals(YOUTUBE, pricedStock.getMarket());
        assertEquals("channel", pricedStock.getSymbol());
        assertEquals("title", pricedStock.getName());
        assertNull(pricedStock.getProfileImage());
        assertNotNull(pricedStock.getTimestamp());
        assertEquals(6, pricedStock.getPrice());
    }

    @Test
    public void testToPricedStockNullDefaultThumbnail() {
        Channel channel = getChannel("channel", 50_000, "title", null);
        channel.getSnippet().getThumbnails().setDefault(null);
        PricedStock pricedStock = toPricedStock(channel);

        assertEquals(YOUTUBE, pricedStock.getMarket());
        assertEquals("channel", pricedStock.getSymbol());
        assertEquals("title", pricedStock.getName());
        assertNull(pricedStock.getProfileImage());
        assertNotNull(pricedStock.getTimestamp());
        assertEquals(6, pricedStock.getPrice());
    }

    @Test
    public void testGetUpdateRunnable() throws ExecutionException, InterruptedException, IOException {
        Channel channel1 = getChannel("1", 50_000, "Channel 1", "link1");
        Channel channel2 = getChannel("2", 100_000, "Channel 2", "link2");
        YouTubeService youTubeService = mock(YouTubeService.class);
        when(youTubeService.getChannels(any())).thenReturn(asList(channel1, channel2));

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<PricedStock> entries = new ArrayList<>();

        YouTubeRemoteStockService youTubeRemoteStockService = new YouTubeRemoteStockService(youTubeService);
        Future<?> future;
        try (StockUpdateRunnable runnable = youTubeRemoteStockService.getUpdateRunnable(executorService, entries::add)) {
            runnable.accept(new Stock().setMarket(YOUTUBE).setSymbol("1").setName("name").setProfileImage("link"));
            runnable.accept(new Stock().setMarket(YOUTUBE).setSymbol("2").setName("name").setProfileImage("link"));
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
        assertEquals("link1", entries.get(0).getProfileImage());
        assertEquals(6, entries.get(0).getPrice());

        assertEquals("2", entries.get(1).getSymbol());
        assertEquals("Channel 2", entries.get(1).getName());
        assertEquals("link2", entries.get(1).getProfileImage());
        assertEquals(12, entries.get(1).getPrice());
    }

    @Test
    public void testSearch() {
        Channel channel1 = getChannel("1", 50_000, "Channel 1", "link1");
        Channel channel2 = getChannel("2", 100_000, "Channel 2", "link2");

        YouTubeService youTubeService = mock(YouTubeService.class);
        when(youTubeService.search(eq("channel"), eq(20))).thenReturn(asList(channel1, channel2));

        YouTubeRemoteStockService youTubeRemoteStockService = new YouTubeRemoteStockService(youTubeService);
        List<PricedStock> stocks = youTubeRemoteStockService.search("channel", 20);

        assertEquals(2, stocks.size());
        assertEquals(YOUTUBE, stocks.get(0).getMarket());
        assertEquals("1", stocks.get(0).getSymbol());
        assertEquals("Channel 1", stocks.get(0).getName());
        assertEquals("link1", stocks.get(0).getProfileImage());
        assertEquals(6, stocks.get(0).getPrice());

        assertEquals(YOUTUBE, stocks.get(1).getMarket());
        assertEquals("2", stocks.get(1).getSymbol());
        assertEquals("Channel 2", stocks.get(1).getName());
        assertEquals("link2", stocks.get(1).getProfileImage());
        assertEquals(12, stocks.get(1).getPrice());
    }
}
