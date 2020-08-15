package vstocks.service.remote.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelLocalization;
import com.google.api.services.youtube.model.ChannelSnippet;
import com.google.api.services.youtube.model.ChannelStatistics;
import org.junit.Test;
import vstocks.model.Stock;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static vstocks.model.Market.YOUTUBE;

public class YouTubeStockUpdateBatchRunnableTest {
    private static Channel getChannel(String id, int subscribers, String title) {
        ChannelSnippet channelSnippet = new ChannelSnippet()
                .setLocalized(new ChannelLocalization().setTitle(title));
        ChannelStatistics channelStatistics = new ChannelStatistics()
                .setSubscriberCount(new BigInteger(String.valueOf(subscribers)));
        return new Channel().setId(id).setSnippet(channelSnippet).setStatistics(channelStatistics);
    }

    @Test
    public void testNormalOperation() {
        Channel channel1 = getChannel("channel1", 50_000, "Channel 1");
        Channel channel2 = getChannel("channel2", 100_000, "Channel 2");
        YouTubeService youTubeService = mock(YouTubeService.class);
        when(youTubeService.getChannels(any())).thenReturn(asList(channel1, channel2));

        List<Stock> stocks = Stream.of("channel1", "channel2")
                .map(symbol -> new Stock().setMarket(YOUTUBE).setSymbol(symbol).setName("Name"))
                .collect(toList());
        List<Channel> channels = new ArrayList<>();
        new YouTubeStockUpdateBatchRunnable(youTubeService, channels::add, stocks).run();

        assertEquals(2, channels.size());
    }

    @Test
    public void testChannelNotFound() {
        YouTubeService youTubeService = mock(YouTubeService.class);
        when(youTubeService.getChannels(any())).thenReturn(emptyList());

        List<Stock> stocks = Stream.of("channel1", "channel2")
                .map(symbol -> new Stock().setMarket(YOUTUBE).setSymbol(symbol).setName("Name"))
                .collect(toList());
        List<Channel> channels = new ArrayList<>();
        new YouTubeStockUpdateBatchRunnable(youTubeService, channels::add, stocks).run();

        assertTrue(channels.isEmpty());
    }

    @Test
    public void testUnknownFailure() {
        YouTubeService youTubeService = mock(YouTubeService.class);
        when(youTubeService.getChannels(any())).thenThrow(new RuntimeException());

        List<Stock> stocks = Stream.of("channel1", "channel2")
                .map(symbol -> new Stock().setMarket(YOUTUBE).setSymbol(symbol).setName("Name"))
                .collect(toList());
        List<Channel> channels = new ArrayList<>();
        new YouTubeStockUpdateBatchRunnable(youTubeService, channels::add, stocks).run();

        assertTrue(channels.isEmpty());
    }
}
