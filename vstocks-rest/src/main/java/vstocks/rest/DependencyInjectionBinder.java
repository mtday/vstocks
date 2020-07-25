package vstocks.rest;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import vstocks.db.ExchangeStore;
import vstocks.db.impl.JdbcExchangeStore;

import javax.sql.DataSource;

import static vstocks.config.Config.*;

public class DependencyInjectionBinder extends AbstractBinder {
    @Override
    protected void configure() {
        DataSource dataSource = getDataSource();
        bind(dataSource).to(DataSource.class);

        ExchangeStore exchangeStore = new JdbcExchangeStore(dataSource);
        bind(exchangeStore).to(ExchangeStore.class);
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
        config.setAutoCommit(true);

        HikariDataSource dataSource = new HikariDataSource(config);
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }
}
