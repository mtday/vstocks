package vstocks.achievement.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import vstocks.achievement.DataSourceExternalResource;
import vstocks.db.DBFactory;
import vstocks.db.jdbc.JdbcDBFactory;
import vstocks.db.jdbc.table.*;
import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static vstocks.model.Market.*;

public abstract class BaseAchievementProviderIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();


    public final Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
    public final User user = new User().setEmail("testuser@domain.com").setUsername("testuser").setDisplayName("User");
    public final Stock twitterStock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setActive(true);
    public final StockPrice twitterStockPrice = new StockPrice().setMarket(TWITTER).setSymbol("symbol").setPrice(10).setTimestamp(now);
    public final Stock youtubeStock = new Stock().setMarket(YOUTUBE).setSymbol("symbol").setName("Name").setActive(true);
    public final StockPrice youtubeStockPrice = new StockPrice().setMarket(YOUTUBE).setSymbol("symbol").setPrice(10).setTimestamp(now);
    public final Stock instagramStock = new Stock().setMarket(INSTAGRAM).setSymbol("symbol").setName("Name").setActive(true);
    public final StockPrice instagramStockPrice = new StockPrice().setMarket(INSTAGRAM).setSymbol("symbol").setPrice(10).setTimestamp(now);
    public final Stock twitchStock = new Stock().setMarket(TWITCH).setSymbol("symbol").setName("Name").setActive(true);
    public final StockPrice twitchStockPrice = new StockPrice().setMarket(TWITCH).setSymbol("symbol").setPrice(10).setTimestamp(now);
    public final Stock facebookStock = new Stock().setMarket(FACEBOOK).setSymbol("symbol").setName("Name").setActive(true);
    public final StockPrice facebookStockPrice = new StockPrice().setMarket(FACEBOOK).setSymbol("symbol").setPrice(10).setTimestamp(now);

    public DBFactory getDBFactory() {
        return new JdbcDBFactory(dataSourceExternalResource.get());
    }

    @Before
    public void setup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            new UserTable().add(connection, user);
            new StockTable().add(connection, twitterStock);
            new StockTable().add(connection, youtubeStock);
            new StockTable().add(connection, instagramStock);
            new StockTable().add(connection, twitchStock);
            new StockTable().add(connection, facebookStock);
            new StockPriceTable().add(connection, twitterStockPrice);
            new StockPriceTable().add(connection, youtubeStockPrice);
            new StockPriceTable().add(connection, instagramStockPrice);
            new StockPriceTable().add(connection, twitchStockPrice);
            new StockPriceTable().add(connection, facebookStockPrice);
            connection.commit();;
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            new ActivityLogTable().truncate(connection);
            new StockTable().truncate(connection);
            new StockPriceTable().truncate(connection);
            new UserAchievementTable().truncate(connection);
            new UserBalanceTable().truncate(connection);
            new UserStockTable().truncate(connection);
            new UserTable().truncate(connection);
            connection.commit();
        }
    }
}
