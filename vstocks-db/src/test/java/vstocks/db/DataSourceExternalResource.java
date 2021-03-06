package vstocks.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.junit.rules.ExternalResource;

import javax.sql.DataSource;
import java.util.function.Supplier;

import static java.util.Optional.ofNullable;
import static vstocks.config.Config.*;

public class DataSourceExternalResource extends ExternalResource implements Supplier<DataSource> {
    private HikariDataSource dataSource;

    @Override
    public DataSource get() {
        return dataSource;
    }

    @Override
    public void before() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(DB_URL.getString() + "test");
        config.setDriverClassName(DB_DRIVER.getString());
        config.setUsername(DB_USER.getString());
        config.setPassword(DB_PASS.getString());
        config.setAutoCommit(false);

        dataSource = new HikariDataSource(config);
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.clean();
        flyway.migrate();
    }

    @Override
    public void after() {
        ofNullable(dataSource).ifPresent(HikariDataSource::close);
    }
}
