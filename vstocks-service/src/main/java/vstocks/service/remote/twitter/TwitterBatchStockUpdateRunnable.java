package vstocks.service.remote.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import vstocks.model.Stock;

import java.util.List;
import java.util.function.Consumer;

class TwitterBatchStockUpdateRunnable implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterBatchStockUpdateRunnable.class);

    private final Twitter twitter;
    private final Consumer<User> userConsumer;
    private final List<Stock> stocks;

    public TwitterBatchStockUpdateRunnable(Twitter twitter, Consumer<User> userConsumer, List<Stock> stocks) {
        this.twitter = twitter;
        this.userConsumer = userConsumer;
        this.stocks = stocks;
    }

    @Override
    public void run() {
        try {
            String[] usernames = stocks.stream().map(Stock::getSymbol).toArray(String[]::new);
            ResponseList<User> responseList = twitter.users().lookupUsers(usernames);
            responseList.forEach(userConsumer);
        } catch (TwitterException e) {
            // Ignore user-not-found errors
            if (e.getStatusCode() != 404) {
                LOGGER.error("Failed to lookup users", e);
            }
        }
    }
}
