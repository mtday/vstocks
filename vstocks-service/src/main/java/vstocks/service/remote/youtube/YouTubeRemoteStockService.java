package vstocks.service.remote.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vstocks.model.PricedStock;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.remote.PriceCalculator;
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
    private final PriceCalculator<Channel> priceCalculator;

    public YouTubeRemoteStockService() {
        this(new YouTubeService(), new YouTubePriceCalculator());
    }

    YouTubeRemoteStockService(YouTubeService youTubeService, PriceCalculator<Channel> priceCalculator) {
        this.youTubeService = youTubeService;
        this.priceCalculator = priceCalculator;
    }

    static PricedStock toPricedStock(Channel channel, PriceCalculator<Channel> priceCalculator) {
        PricedStock pricedStock = new PricedStock()
                .setMarket(YOUTUBE)
                .setSymbol(channel.getId())
                .setName(channel.getSnippet().getLocalized().getTitle())
                .setTimestamp(Instant.now())
                .setPrice(priceCalculator.getPrice(channel));
        Stream.of(channel.getSnippet().getThumbnails())
                .filter(Objects::nonNull)
                .map(ThumbnailDetails::getDefault)
                .filter(Objects::nonNull)
                .map(Thumbnail::getUrl)
                .filter(Objects::nonNull)
                .findFirst()
                .ifPresent(pricedStock::setProfileImage);
        return pricedStock;
    }

    @Override
    public StockUpdateRunnable getUpdateRunnable(ExecutorService executorService,
                                                 Consumer<PricedStock> updateConsumer) {
        return new YouTubeStockUpdateRunnable(youTubeService, executorService,
                channel -> updateConsumer.accept(toPricedStock(channel, priceCalculator)));
    }

    @Override
    public List<PricedStock> search(String search, int limit) {
        return youTubeService.search(search, limit).stream()
                .map(user -> toPricedStock(user, priceCalculator))
                .collect(toList());
    }
}
