package vstocks.service.remote.youtube;

import com.google.api.services.youtube.model.*;
import org.junit.Test;
import vstocks.model.PricedStock;
import vstocks.model.Stock;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.remote.PriceCalculator;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.service.remote.youtube.YouTubeRemoteStockService.toPricedStock;

public class YouTubeRemoteStockServiceTest {
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
        PriceCalculator<Channel> priceCalculator = channel -> 5;
        PricedStock pricedStock = toPricedStock(getChannel("channel", 50_000, "title", "link"), priceCalculator);

        assertEquals(YOUTUBE, pricedStock.getMarket());
        assertEquals("channel", pricedStock.getSymbol());
        assertEquals("title", pricedStock.getName());
        assertEquals("link", pricedStock.getProfileImage());
        assertNotNull(pricedStock.getTimestamp());
        assertEquals(5, pricedStock.getPrice());
    }

    @Test
    public void testToPricedStockNullUrl() {
        PriceCalculator<Channel> priceCalculator = channel -> 5;
        PricedStock pricedStock = toPricedStock(getChannel("channel", 50_000, "title", null), priceCalculator);

        assertEquals(YOUTUBE, pricedStock.getMarket());
        assertEquals("channel", pricedStock.getSymbol());
        assertEquals("title", pricedStock.getName());
        assertNull(pricedStock.getProfileImage());
        assertNotNull(pricedStock.getTimestamp());
        assertEquals(5, pricedStock.getPrice());
    }

    @Test
    public void testToPricedStockNullThumbnailDetails() {
        PriceCalculator<Channel> priceCalculator = channel -> 5;
        Channel channel = getChannel("channel", 50_000, "title", null);
        channel.getSnippet().setThumbnails(null);
        PricedStock pricedStock = toPricedStock(channel, priceCalculator);

        assertEquals(YOUTUBE, pricedStock.getMarket());
        assertEquals("channel", pricedStock.getSymbol());
        assertEquals("title", pricedStock.getName());
        assertNull(pricedStock.getProfileImage());
        assertNotNull(pricedStock.getTimestamp());
        assertEquals(5, pricedStock.getPrice());
    }

    @Test
    public void testToPricedStockNullDefaultThumbnail() {
        PriceCalculator<Channel> priceCalculator = channel -> 5;
        Channel channel = getChannel("channel", 50_000, "title", null);
        channel.getSnippet().getThumbnails().setDefault(null);
        PricedStock pricedStock = toPricedStock(channel, priceCalculator);

        assertEquals(YOUTUBE, pricedStock.getMarket());
        assertEquals("channel", pricedStock.getSymbol());
        assertEquals("title", pricedStock.getName());
        assertNull(pricedStock.getProfileImage());
        assertNotNull(pricedStock.getTimestamp());
        assertEquals(5, pricedStock.getPrice());
    }

    @Test
    public void testGetUpdateRunnable() throws ExecutionException, InterruptedException, IOException {
        Channel channel1 = getChannel("1", 50_000, "Channel 1", "link1");
        Channel channel2 = getChannel("2", 100_000, "Channel 2", "link2");
        YouTubeService youTubeService = mock(YouTubeService.class);
        when(youTubeService.getChannels(any())).thenReturn(asList(channel1, channel2));

        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<PricedStock> entries = new ArrayList<>();

        PriceCalculator<Channel> priceCalculator = channel -> 5;
        YouTubeRemoteStockService youTubeRemoteStockService =
                new YouTubeRemoteStockService(youTubeService, priceCalculator);
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
        assertEquals(5, entries.get(0).getPrice());

        assertEquals("2", entries.get(1).getSymbol());
        assertEquals("Channel 2", entries.get(1).getName());
        assertEquals("link2", entries.get(1).getProfileImage());
        assertEquals(5, entries.get(1).getPrice());
    }

    @Test
    public void testSearch() {
        Channel channel1 = getChannel("1", 50_000, "Channel 1", "link1");
        Channel channel2 = getChannel("2", 100_000, "Channel 2", "link2");

        YouTubeService youTubeService = mock(YouTubeService.class);
        when(youTubeService.search(eq("channel"), eq(20))).thenReturn(asList(channel1, channel2));

        PriceCalculator<Channel> priceCalculator = channel -> 5;
        YouTubeRemoteStockService youTubeRemoteStockService =
                new YouTubeRemoteStockService(youTubeService, priceCalculator);
        List<PricedStock> stocks = youTubeRemoteStockService.search("channel", 20);

        assertEquals(2, stocks.size());
        assertEquals(YOUTUBE, stocks.get(0).getMarket());
        assertEquals("1", stocks.get(0).getSymbol());
        assertEquals("Channel 1", stocks.get(0).getName());
        assertEquals("link1", stocks.get(0).getProfileImage());
        assertEquals(5, stocks.get(0).getPrice());

        assertEquals(YOUTUBE, stocks.get(1).getMarket());
        assertEquals("2", stocks.get(1).getSymbol());
        assertEquals("Channel 2", stocks.get(1).getName());
        assertEquals("link2", stocks.get(1).getProfileImage());
        assertEquals(5, stocks.get(1).getPrice());
    }
}
