package vstocks.achievement.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import vstocks.achievement.DataSourceExternalResource;
import vstocks.db.*;
import vstocks.db.ServiceFactoryImpl;
import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

import static java.time.temporal.ChronoUnit.SECONDS;
import static vstocks.model.Market.*;
import static vstocks.model.User.generateId;

public abstract class BaseAchievementProviderIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();


    public final Instant now = Instant.now().truncatedTo(SECONDS);
    public final User user = new User().setId(generateId("testuser@domain.com")).setEmail("testuser@domain.com").setUsername("testuser").setDisplayName("User");
    public final Stock twitterStock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setProfileImage("link");
    public final StockPrice twitterStockPrice = new StockPrice().setMarket(TWITTER).setSymbol("symbol").setPrice(10).setTimestamp(now);
    public final Stock youtubeStock = new Stock().setMarket(YOUTUBE).setSymbol("symbol").setName("Name").setProfileImage("link");
    public final StockPrice youtubeStockPrice = new StockPrice().setMarket(YOUTUBE).setSymbol("symbol").setPrice(10).setTimestamp(now);
    public final Stock instagramStock = new Stock().setMarket(INSTAGRAM).setSymbol("symbol").setName("Name").setProfileImage("link");
    public final StockPrice instagramStockPrice = new StockPrice().setMarket(INSTAGRAM).setSymbol("symbol").setPrice(10).setTimestamp(now);
    public final Stock twitchStock = new Stock().setMarket(TWITCH).setSymbol("symbol").setName("Name").setProfileImage("link");
    public final StockPrice twitchStockPrice = new StockPrice().setMarket(TWITCH).setSymbol("symbol").setPrice(10).setTimestamp(now);
    public final Stock facebookStock = new Stock().setMarket(FACEBOOK).setSymbol("symbol").setName("Name").setProfileImage("link");
    public final StockPrice facebookStockPrice = new StockPrice().setMarket(FACEBOOK).setSymbol("symbol").setPrice(10).setTimestamp(now);

    public ServiceFactory getDBFactory() {
        return new ServiceFactoryImpl(dataSourceExternalResource.get());
    }

    @Before
    public void setup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            new UserDB().add(connection, user);
            new StockDB().add(connection, twitterStock);
            new StockDB().add(connection, youtubeStock);
            new StockDB().add(connection, instagramStock);
            new StockDB().add(connection, twitchStock);
            new StockDB().add(connection, facebookStock);
            new StockPriceDB().add(connection, twitterStockPrice);
            new StockPriceDB().add(connection, youtubeStockPrice);
            new StockPriceDB().add(connection, instagramStockPrice);
            new StockPriceDB().add(connection, twitchStockPrice);
            new StockPriceDB().add(connection, facebookStockPrice);
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            new ActivityLogTable().truncate(connection);
            new StockDB().truncate(connection);
            new StockPriceDB().truncate(connection);
            new UserAchievementDB().truncate(connection);
            new UserCreditsDB().truncate(connection);
            new UserStockDB().truncate(connection);
            new UserDB().truncate(connection);
            connection.commit();
        }
    }
}
