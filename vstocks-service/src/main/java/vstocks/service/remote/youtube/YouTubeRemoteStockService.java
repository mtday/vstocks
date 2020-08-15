package vstocks.service.remote.youtube;

import com.google.api.services.youtube.model.Channel;
import vstocks.model.PricedStock;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.remote.RemoteStockService;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;
import static vstocks.model.Market.YOUTUBE;

public class YouTubeRemoteStockService implements RemoteStockService {
    private final YouTubeService youTubeService;

    public YouTubeRemoteStockService() {
        this(new YouTubeService());
    }

    YouTubeRemoteStockService(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    static int getPrice(Channel channel) {
        double subscribers = channel.getStatistics().getSubscriberCount().doubleValue();
        return (int) (((500_000 + subscribers) / (1_000_000 + subscribers)) * 10_000 - 5_000 + 1);
    }

    static PricedStock toPricedStock(Channel channel) {
        return new PricedStock()
                .setMarket(YOUTUBE)
                .setSymbol(channel.getId())
                .setName(channel.getSnippet().getLocalized().getTitle())
                .setTimestamp(Instant.now())
                .setPrice(getPrice(channel));
    }

    @Override
    public StockUpdateRunnable getUpdateRunnable(ExecutorService executorService,
                                                 Consumer<PricedStock> updateConsumer) {
        return new YouTubeStockUpdateRunnable(youTubeService, executorService,
                channel -> updateConsumer.accept(toPricedStock(channel)));
    }

    @Override
    public List<PricedStock> search(String search, int limit) {
        return youTubeService.search(search, limit).stream()
                .map(YouTubeRemoteStockService::toPricedStock)
                .collect(toList());
    }
}
