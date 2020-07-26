package vstocks.rest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import vstocks.db.service.*;
import vstocks.db.service.impl.*;
import vstocks.db.store.*;
import vstocks.db.store.impl.*;

import javax.sql.DataSource;

import static vstocks.config.Config.*;

public class DependencyInjectionBinder extends AbstractBinder {
    @Override
    protected void configure() {
        DataSource dataSource = getDataSource();

        ActivityLogStore activityLogStore = new JdbcActivityLogStore();
        ActivityLogService activityLogService = new DefaultActivityLogService(dataSource, activityLogStore);
        bind(activityLogService).to(ActivityLogService.class);

        MarketStore marketStore = new JdbcMarketStore();
        MarketService marketService = new DefaultMarketService(dataSource, marketStore);
        bind(marketService).to(MarketService.class);

        StockPriceStore stockPriceStore = new JdbcStockPriceStore();
        StockPriceService stockPriceService = new DefaultStockPriceService(dataSource, stockPriceStore);
        bind(stockPriceService).to(StockPriceService.class);

        StockStore stockStore = new JdbcStockStore();
        StockService stockService = new DefaultStockService(dataSource, stockStore);
        bind(stockService).to(StockService.class);

        UserBalanceStore userBalanceStore = new JdbcUserBalanceStore();
        UserBalanceService userBalanceService = new DefaultUserBalanceService(dataSource, userBalanceStore);
        bind(userBalanceService).to(UserBalanceService.class);

        UserStore userStore = new JdbcUserStore();
        UserService userService = new DefaultUserService(dataSource, userStore);
        bind(userService).to(UserService.class);

        UserStockStore userStockStore = new JdbcUserStockStore();
        UserStockService userStockService = new DefaultUserStockService(
                dataSource, userStockStore, userBalanceStore, stockPriceStore, activityLogStore);
        bind(userStockService).to(UserStockService.class);
    }

    private DataSource getDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL.getString());
        config.setDriverClassName(DB_DRIVER.getString());
        config.setUsername(DB_USER.getString());
        config.setPassword(DB_PASS.getString());
        config.setMinimumIdle(DB_MIN_IDLE.getInt());
        config.setMaximumPoolSize(DB_MAX_POOL_SIZE.getInt());
        config.setIdleTimeout(DB_IDLE_TIMEOUT.getLong());
        config.setConnectionTimeout(DB_CONNECTION_TIMEOUT.getLong());
        config.setAutoCommit(false);

        HikariDataSource dataSource = new HikariDataSource(config);
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }
}
