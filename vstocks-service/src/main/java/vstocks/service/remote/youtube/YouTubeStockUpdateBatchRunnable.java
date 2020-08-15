package vstocks.service.remote.youtube;

import com.google.api.services.youtube.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.model.Stock;

import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toSet;

class YouTubeStockUpdateBatchRunnable implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeStockUpdateBatchRunnable.class);

    private final YouTubeService youTubeService;
    private final Consumer<Channel> channelConsumer;
    private final List<Stock> stocks;

    public YouTubeStockUpdateBatchRunnable(YouTubeService youTubeService,
                                           Consumer<Channel> channelConsumer,
                                           List<Stock> stocks) {
        this.youTubeService = youTubeService;
        this.channelConsumer = channelConsumer;
        this.stocks = stocks;
    }

    List<Stock> getStocks() {
        return stocks;
    }

    @Override
    public void run() {
        try {
            youTubeService.getChannels(stocks.stream().map(Stock::getSymbol).collect(toSet())).forEach(channelConsumer);
        } catch (Throwable e) {
            LOGGER.error("Unexpected error", e);
        }
    }
}
