package vstocks.achievement.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import vstocks.achievement.DataSourceExternalResource;
import vstocks.db.ServiceFactory;
import vstocks.db.ServiceFactoryImpl;
import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.model.User;

import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static vstocks.model.Market.*;
import static vstocks.model.User.generateId;

public abstract class BaseAchievementProviderIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private ServiceFactory serviceFactory;

    public final Instant now = Instant.now().truncatedTo(SECONDS);

    public final User user = new User()
            .setId(generateId("testuser@domain.com"))
            .setEmail("testuser@domain.com")
            .setUsername("testuser")
            .setDisplayName("User");

    public final Stock twitterStock = new Stock()
            .setMarket(TWITTER)
            .setSymbol("symbol")
            .setName("Name")
            .setProfileImage("link");
    public final StockPrice twitterStockPrice = new StockPrice()
            .setMarket(TWITTER)
            .setSymbol("symbol")
            .setPrice(10)
            .setTimestamp(now);

    public final Stock youtubeStock = new Stock()
            .setMarket(YOUTUBE)
            .setSymbol("symbol")
            .setName("Name")
            .setProfileImage("link");
    public final StockPrice youtubeStockPrice = new StockPrice()
            .setMarket(YOUTUBE)
            .setSymbol("symbol")
            .setPrice(10)
            .setTimestamp(now);

    public final Stock instagramStock = new Stock()
            .setMarket(INSTAGRAM)
            .setSymbol("symbol")
            .setName("Name")
            .setProfileImage("link");
    public final StockPrice instagramStockPrice = new StockPrice()
            .setMarket(INSTAGRAM)
            .setSymbol("symbol")
            .setPrice(10)
            .setTimestamp(now);

    public final Stock twitchStock = new Stock()
            .setMarket(TWITCH)
            .setSymbol("symbol")
            .setName("Name")
            .setProfileImage("link");
    public final StockPrice twitchStockPrice = new StockPrice()
            .setMarket(TWITCH)
            .setSymbol("symbol")
            .setPrice(10)
            .setTimestamp(now);

    public final Stock facebookStock = new Stock()
            .setMarket(FACEBOOK)
            .setSymbol("symbol")
            .setName("Name")
            .setProfileImage("link");
    public final StockPrice facebookStockPrice = new StockPrice()
            .setMarket(FACEBOOK)
            .setSymbol("symbol")
            .setPrice(10)
            .setTimestamp(now);

    public ServiceFactory getDBFactory() {
        return new ServiceFactoryImpl(dataSourceExternalResource.get());
    }

    @Before
    public void setup() {
        serviceFactory = new ServiceFactoryImpl(dataSourceExternalResource.get());

        serviceFactory.getUserService().add(user);
        serviceFactory.getStockService().add(twitterStock);
        serviceFactory.getStockService().add(youtubeStock);
        serviceFactory.getStockService().add(instagramStock);
        serviceFactory.getStockService().add(twitchStock);
        serviceFactory.getStockService().add(facebookStock);
        serviceFactory.getStockPriceService().add(twitterStockPrice);
        serviceFactory.getStockPriceService().add(youtubeStockPrice);
        serviceFactory.getStockPriceService().add(instagramStockPrice);
        serviceFactory.getStockPriceService().add(twitchStockPrice);
        serviceFactory.getStockPriceService().add(facebookStockPrice);
    }

    @After
    public void cleanup() {
        serviceFactory.getActivityLogService().truncate();
        serviceFactory.getStockService().truncate();
        serviceFactory.getStockPriceService().truncate();
        serviceFactory.getUserAchievementService().truncate();
        serviceFactory.getUserCreditsService().truncate();
        serviceFactory.getUserStockService().truncate();
        serviceFactory.getUserService().truncate();
    }
}
