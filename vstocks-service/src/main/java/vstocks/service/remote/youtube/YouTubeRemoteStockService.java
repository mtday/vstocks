package vstocks.service.remote.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.model.PricedStock;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.remote.RemoteStockService;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static vstocks.model.Market.YOUTUBE;

public class YouTubeRemoteStockService implements RemoteStockService {
    private static final Logger LOGGER = LoggerFactory.getLogger(YouTubeRemoteStockService.class);

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
        PricedStock pricedStock = new PricedStock()
                .setMarket(YOUTUBE)
                .setSymbol(channel.getId())
                .setName(channel.getSnippet().getLocalized().getTitle())
                .setTimestamp(Instant.now())
                .setPrice(getPrice(channel));
        Stream.of(channel.getSnippet().getThumbnails())
                .filter(Objects::nonNull)
                .map(ThumbnailDetails::getDefault)
                .filter(Objects::nonNull)
                .map(Thumbnail::getUrl)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(pricedStock::setImageLink);
        return pricedStock;
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
