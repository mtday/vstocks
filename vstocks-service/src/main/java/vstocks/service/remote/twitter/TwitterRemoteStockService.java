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
        double followers = (double) user.getFollowersCount();
        LOGGER.info("Twitter user {} has {} followers", user.getScreenName(), user.getFollowersCount());
        // Scale the number of followers into the range of (-0.8, 4), using the arbitrary estimation of
        // 0 and 30_000_000 as the minimum and maximum number of followers for an account.
        // The -0.8f determines how slowly the price ramps up at the beginning. We need it to scale up slowly
        // to prevent users from "gaming" the system by mass following in concert to impact the price.
        double scaledMin = -0.8f, scaledMax = 4f;
        int max = 30_000_000;
        double scaledFollowers = followers * (scaledMax - scaledMin) / max;

        // Use a logistic function to apply a sigmoid shape to the price.
        double f = 1 / (1 + Math.pow(Math.E, -scaledFollowers));

        // Shift the sigmoid up by 0.5 to make the min ~0.0 and the max ~1.0
        f = f - 0.5;

        // Scale the price onto the sigmoid function result.
        int maxPrice = 5_000;
        return (int) (f * maxPrice * 2) + 1; // Add 1 so we don't get any 0 prices.
    }

    static PricedStock toPricedStock(User user) {
        return new PricedStock()
                .setMarket(TWITTER)
                .setSymbol(user.getScreenName())
                .setName(user.getName())
                .setProfileImage(user.getProfileImageURLHttps())
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
