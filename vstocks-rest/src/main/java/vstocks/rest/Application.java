package vstocks.rest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.pac4j.jax.rs.features.JaxRsConfigProvider;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;
import vstocks.rest.security.AccessLogFilter;
import vstocks.rest.security.SecurityConfig;
import vstocks.rest.task.MemoryUsageLoggingTask;
import vstocks.rest.task.StockPriceAgeOffTask;
import vstocks.rest.task.StockUpdateTask;
import vstocks.service.db.DatabaseServiceFactory;
import vstocks.service.db.jdbc.JdbcDatabaseServiceFactory;
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
    public Application() {
        this(null, true, true);
    }

    public Application(DataSource dataSource, boolean includePac4j, boolean includeBackgroundTasks) {
        RemoteStockServiceFactory remoteStockServiceFactory = new RemoteStockServiceFactory();
        DatabaseServiceFactory databaseServiceFactory =
                new JdbcDatabaseServiceFactory(ofNullable(dataSource).orElseGet(this::getDataSource));

        property("jersey.config.server.wadl.disableWadl", "true");
        packages(true, Application.class.getPackageName());
        register(new AccessLogFilter());
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(databaseServiceFactory).to(DatabaseServiceFactory.class);
            }
        });

        if (includePac4j) {
            register(new JaxRsConfigProvider(SecurityConfig.getConfig()));
            register(new Pac4JSecurityFeature());
            register(new Pac4JValueFactoryProvider.Binder());
            register(new ServletJaxRsContextFactoryProvider());
        }

        if (includeBackgroundTasks) {
            // This executor is used to run the scheduled background tasks.
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
            // This executor is used to run stock price lookup tasks.
            ExecutorService stockPriceLookupExecutorService = Executors.newFixedThreadPool(8);

            new MemoryUsageLoggingTask().schedule(scheduledExecutorService);
            new StockPriceAgeOffTask(databaseServiceFactory).schedule(scheduledExecutorService);
            new StockUpdateTask(remoteStockServiceFactory, databaseServiceFactory, stockPriceLookupExecutorService).schedule(scheduledExecutorService);
        }
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
