package vstocks.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.pac4j.jax.rs.features.JaxRsConfigProvider;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;
import vstocks.db.DBFactory;
import vstocks.db.jdbc.JdbcDBFactory;
import vstocks.rest.exception.BadRequestExceptionMapper;
import vstocks.rest.exception.NotFoundExceptionMapper;
import vstocks.rest.resource.v1.market.GetAllMarkets;
import vstocks.rest.resource.v1.market.GetMarket;
import vstocks.rest.resource.v1.market.stock.*;
import vstocks.rest.resource.v1.security.Callback;
import vstocks.rest.resource.v1.security.Login;
import vstocks.rest.resource.v1.security.Logout;
import vstocks.rest.resource.v1.user.GetUser;
import vstocks.rest.resource.v1.user.UserReset;
import vstocks.rest.security.AccessLogFilter;
import vstocks.rest.security.SecurityConfig;
import vstocks.rest.task.MemoryUsageLoggingTask;
import vstocks.rest.task.PortfolioValueAgeOffTask;
import vstocks.rest.task.StockPriceAgeOffTask;
import vstocks.rest.task.StockUpdateTask;
import vstocks.service.remote.DefaultRemoteStockServiceFactory;
import vstocks.service.remote.RemoteStockServiceFactory;

import javax.sql.DataSource;
import javax.ws.rs.ApplicationPath;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.Optional.ofNullable;
import static vstocks.config.Config.*;

@ApplicationPath("/")
public class Application extends ResourceConfig {
    @SuppressWarnings("unused")
    public Application() {
        this(new Environment()
                        .setRemoteStockServiceFactory(new DefaultRemoteStockServiceFactory())
                        .setDBFactory(new JdbcDBFactory(getDataSource()))
                        .setIncludeSecurity(true)
                        .setIncludeBackgroundTasks(true));
    }

    public Application(Environment environment) {
        property("jersey.config.server.wadl.disableWadl", "true");

        register(AddStock.class);
        register(BuyStock.class);
        register(GetStock.class);
        register(GetStocksForMarket.class);
        register(SearchStocks.class);
        register(SellStock.class);
        register(GetAllMarkets.class);
        register(GetMarket.class);

        register(Callback.class);
        register(Login.class);
        register(Logout.class);

        register(GetUser.class);
        register(UserReset.class);

        register(BadRequestExceptionMapper.class);
        register(NotFoundExceptionMapper.class);

        register(AccessLogFilter.class);
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(getObjectMapper()).to(ObjectMapper.class);
                ofNullable(environment.getDBFactory()).ifPresent(d -> bind(d).to(DBFactory.class));
                ofNullable(environment.getRemoteStockServiceFactory()).ifPresent(r -> bind(r).to(RemoteStockServiceFactory.class));
            }
        });

        if (environment.isIncludeSecurity()) {
            register(new JaxRsConfigProvider(SecurityConfig.getConfig()));
            register(new Pac4JSecurityFeature());
            register(new Pac4JValueFactoryProvider.Binder());
            register(new ServletJaxRsContextFactoryProvider());
        }

        if (environment.isIncludeBackgroundTasks()) {
            // This executor is used to run the scheduled background tasks.
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
            // This executor is used to run stock price lookup tasks.
            ExecutorService stockPriceLookupExecutorService = Executors.newFixedThreadPool(8);

            new MemoryUsageLoggingTask().schedule(scheduledExecutorService);
            new PortfolioValueAgeOffTask(environment).schedule(scheduledExecutorService);
            new StockPriceAgeOffTask(environment).schedule(scheduledExecutorService);
            new StockUpdateTask(environment, stockPriceLookupExecutorService).schedule(scheduledExecutorService);
        }
    }

    private static ObjectMapper getObjectMapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private static DataSource getDataSource() {
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
