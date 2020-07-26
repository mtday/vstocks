package vstocks.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.rules.ExternalResource;

import javax.sql.DataSource;
import java.util.function.Supplier;

import static vstocks.config.Config.*;

public class DataSourceExternalResource extends ExternalResource implements Supplier<DataSource> {
    private DataSource dataSource;

    @Override
    public DataSource get() {
        return dataSource;
    }

    @Override
    public void before() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL.getString());
        config.setDriverClassName(DB_DRIVER.getString());
        config.setUsername(DB_USER.getString());
        config.setPassword(DB_PASS.getString());
        config.setAutoCommit(false);

        dataSource = new HikariDataSource(config);
        Flyway.configure().dataSource(dataSource).load().migrate();
    }
}
