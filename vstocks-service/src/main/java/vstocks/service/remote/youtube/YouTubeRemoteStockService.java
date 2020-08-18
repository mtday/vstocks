package vstocks.service.remote.youtube;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Thumbnail;
import com.google.api.services.youtube.model.ThumbnailDetails;
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
    private final YouTubeService youTubeService;

    public YouTubeRemoteStockService() {
        this(new YouTubeService());
    }

    YouTubeRemoteStockService(YouTubeService youTubeService) {
        this.youTubeService = youTubeService;
    }

    static int getPrice(Channel channel) {
        double subscribers = channel.getStatistics().getSubscriberCount().doubleValue();
        // Scale the number of subscribers into the range of (-0.8, 4), using the arbitrary estimation of
        // 0 and 100_000_000 as the minimum and maximum number of subscribers for an account.
        // The -0.8f determines how slowly the price ramps up at the beginning. We need it to scale up slowly
        // to prevent users from "gaming" the system by mass subscribing in concert to impact the price.
        float scaledMin = -0.8f, scaledMax = 4f;
        int max = 100_000_000;
        double scaledFollowers = subscribers * (scaledMax - scaledMin) / max;

        // Use a logistic function to apply a sigmoid shape to the price.
        double f = 1 / (1 + Math.pow(Math.E, -scaledFollowers));

        // Shift the sigmoid up by 0.5 to make the min ~0.0 and the max ~1.0
        f = f - 0.5;

        // Scale the price onto the sigmoid function result.
        int maxPrice = 5_000;
        return (int) (f * maxPrice * 2) + 1; // Add 1 so we don't get any 0 prices.
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
                .ifPresent(pricedStock::setProfileImage);
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
