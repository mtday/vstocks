package vstocks.service.remote.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import vstocks.model.PricedStock;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.remote.RemoteStockService;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static vstocks.config.Config.*;
import static vstocks.model.Market.TWITTER;

public class TwitterRemoteStockService implements RemoteStockService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterRemoteStockService.class);
    private final Twitter twitter;

    public TwitterRemoteStockService() {
        this(new TwitterFactory(createConfiguration()).getInstance());
    }

    TwitterRemoteStockService(Twitter twitter) {
        this.twitter = twitter;
    }

    static Configuration createConfiguration() {
        return new ConfigurationBuilder()
                .setOAuthConsumerKey(TWITTER_API_CONSUMER_KEY.getString())
                .setOAuthConsumerSecret(TWITTER_API_CONSUMER_SECRET.getString())
                .setOAuthAccessToken(TWITTER_API_ACCESSTOKEN_KEY.getString())
                .setOAuthAccessTokenSecret(TWITTER_API_ACCESSTOKEN_SECRET.getString())
                .build();
    }

    static int getPrice(User user) {
        float followers = (float) user.getFollowersCount();
        return (int) (((500_000 + followers) / (1_000_000 + followers)) * 10_000 - 5_000 + 1);
    }

    static PricedStock toPricedStock(User user) {
        return new PricedStock()
                .setMarket(TWITTER)
                .setSymbol(user.getScreenName())
                .setName(user.getName())
                .setImageLink(user.getProfileImageURLHttps())
                .setTimestamp(Instant.now())
                .setPrice(getPrice(user));
    }

    @Override
    public StockUpdateRunnable getUpdateRunnable(ExecutorService executorService,
                                                 Consumer<PricedStock> updateConsumer) {
        return new TwitterStockUpdateRunnable(twitter, executorService,
                user -> updateConsumer.accept(toPricedStock(user)));
    }

    @Override
    public List<PricedStock> search(String search, int limit) {
        try {
            return twitter.users().searchUsers(search, limit).stream()
                    .map(TwitterRemoteStockService::toPricedStock)
                    .collect(toList());
        } catch (TwitterException e) {
            LOGGER.error("Failed to search twitter user accounts: {}", search, e);
            return emptyList();
        }
    }
}
