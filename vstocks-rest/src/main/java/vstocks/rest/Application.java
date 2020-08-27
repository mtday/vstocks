package vstocks.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.pac4j.jax.rs.features.JaxRsConfigProvider;
import org.pac4j.jax.rs.features.Pac4JSecurityFeature;
import org.pac4j.jax.rs.jersey.features.Pac4JValueFactoryProvider;
import org.pac4j.jax.rs.servlet.features.ServletJaxRsContextFactoryProvider;
import vstocks.achievement.AchievementService;
import vstocks.db.ServiceFactory;
import vstocks.db.ServiceFactoryImpl;
import vstocks.rest.exception.BadRequestExceptionMapper;
import vstocks.rest.exception.NotFoundExceptionMapper;
import vstocks.rest.resource.achievement.GetAchievements;
import vstocks.rest.resource.dashboard.*;
import vstocks.rest.resource.market.GetAllMarkets;
import vstocks.rest.resource.market.GetMarket;
import vstocks.rest.resource.market.stock.*;
import vstocks.rest.resource.security.Callback;
import vstocks.rest.resource.security.Login;
import vstocks.rest.resource.security.Logout;
import vstocks.rest.resource.system.GetActiveTransactionCount;
import vstocks.rest.resource.system.GetActiveUserCount;
import vstocks.rest.resource.system.GetTotalTransactionCount;
import vstocks.rest.resource.system.GetTotalUserCount;
import vstocks.rest.resource.user.CheckUsername;
import vstocks.rest.resource.user.GetUser;
import vstocks.rest.resource.user.PutUser;
import vstocks.rest.resource.user.ResetUser;
import vstocks.rest.resource.user.achievement.GetUserAchievements;
import vstocks.rest.resource.user.portfolio.GetCreditBalance;
import vstocks.rest.resource.user.portfolio.GetStocks;
import vstocks.rest.resource.user.portfolio.rank.*;
import vstocks.rest.security.AccessLogFilter;
import vstocks.rest.security.JwtSecurity;
import vstocks.rest.security.JwtTokenFilter;
import vstocks.rest.security.SecurityConfig;
import vstocks.rest.task.portfolio.CreditRankUpdateTask;
import vstocks.rest.task.portfolio.MarketRankUpdateTask;
import vstocks.rest.task.portfolio.MarketTotalRankUpdateTask;
import vstocks.rest.task.portfolio.TotalRankUpdateTask;
import vstocks.rest.task.stock.StockPriceAgeOffTask;
import vstocks.rest.task.stock.StockUpdateTask;
import vstocks.rest.task.system.*;
import vstocks.service.remote.DefaultRemoteStockServiceFactory;
import vstocks.service.remote.RemoteStockServiceFactory;

import javax.inject.Inject;
import javax.sql.DataSource;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static vstocks.config.Config.*;

@ApplicationPath("/")
public class Application extends ResourceConfig {
    public Application() {
        this(getEnvironment());
    }

    public Application(Environment environment) {
        property("jersey.config.server.wadl.disableWadl", "true");

        // achievement
        register(GetAchievements.class);

        // market
        register(GetAllMarkets.class);
        register(GetMarket.class);

        // market/stock
        register(BuyStock.class);
        register(GetBestStocksForMarket.class);
        register(GetStock.class);
        register(GetStocksForMarket.class);
        register(SearchStocks.class);
        register(SellStock.class);

        // security
        register(Callback.class);
        register(Login.class);
        register(Logout.class);

        // dashboard/standings
        register(GetCreditStandings.class);
        register(GetMarketStandings.class);
        register(GetMarketTotalStandings.class);
        register(GetTotalStandings.class);

        // dashboard/stocks
        register(GetBestStocks.class);

        // dashboard/overall
        register(GetOverallCreditValue.class);
        register(GetOverallMarketValue.class);
        register(GetOverallMarketTotalValue.class);
        register(GetOverallTotalValue.class);

        // system
        register(GetActiveUserCount.class);
        register(GetTotalUserCount.class);
        register(GetActiveTransactionCount.class);
        register(GetTotalTransactionCount.class);

        // user
        register(GetUser.class);
        register(PutUser.class);
        register(ResetUser.class);
        register(CheckUsername.class);

        // user/achievement
        register(GetUserAchievements.class);

        // user/portfolio
        register(GetCreditRank.class);
        register(GetMarketRank.class);
        register(GetMarketRanks.class);
        register(GetMarketTotalRank.class);
        register(GetTotalRank.class);
        register(GetCreditBalance.class);
        register(GetStocks.class);

        register(BadRequestExceptionMapper.class);
        register(NotFoundExceptionMapper.class);

        register(AccessLogFilter.class);
        register(JwtTokenFilter.class);

        register(JsonFeature.class);

        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(getObjectMapper()).to(ObjectMapper.class);
                ofNullable(environment.getServiceFactory()).ifPresent(d -> bind(d).to(ServiceFactory.class));
                ofNullable(environment.getRemoteStockServiceFactory()).ifPresent(r -> bind(r).to(RemoteStockServiceFactory.class));
                ofNullable(environment.getAchievementService()).ifPresent(a -> bind(a).to(AchievementService.class));
                ofNullable(environment.getJwtSecurity()).ifPresent(j -> bind(j).to(JwtSecurity.class));
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
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(8);
            // This executor is used to run stock price lookup tasks.
            ExecutorService stockPriceLookupExecutorService = Executors.newFixedThreadPool(8);

            // Stock tasks
            new StockPriceAgeOffTask(environment).schedule(scheduledExecutorService);
            new StockUpdateTask(environment, stockPriceLookupExecutorService).schedule(scheduledExecutorService);

            // Portfolio tasks
            new CreditRankUpdateTask(environment).schedule(scheduledExecutorService);
            new MarketRankUpdateTask(environment).schedule(scheduledExecutorService);
            new MarketTotalRankUpdateTask(environment).schedule(scheduledExecutorService);
            new TotalRankUpdateTask(environment).schedule(scheduledExecutorService);

            // System tasks
            new MemoryUsageLoggingTask().schedule(scheduledExecutorService);
            new ActiveUserCountUpdateTask(environment).schedule(scheduledExecutorService);
            new TotalUserCountUpdateTask(environment).schedule(scheduledExecutorService);
            new ActiveTransactionCountUpdateTask(environment).schedule(scheduledExecutorService);
            new TotalTransactionCountUpdateTask(environment).schedule(scheduledExecutorService);
            new OverallCreditValueUpdateTask(environment).schedule(scheduledExecutorService);
            new OverallMarketValueUpdateTask(environment).schedule(scheduledExecutorService);
            new OverallMarketTotalValueUpdateTask(environment).schedule(scheduledExecutorService);
            new OverallTotalValueUpdateTask(environment).schedule(scheduledExecutorService);
        }
    }

    private static Environment getEnvironment() {
        return new Environment()
                .setServiceFactory(new ServiceFactoryImpl(getDataSource()))
                .setRemoteStockServiceFactory(new DefaultRemoteStockServiceFactory())
                .setAchievementService(new AchievementService())
                .setJwtSecurity(new JwtSecurity())
                .setIncludeSecurity(true)
                .setIncludeBackgroundTasks(true);
    }

    public static ObjectMapper getObjectMapper() {
        return new ObjectMapper()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(new JavaTimeModule());
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

    static class JsonFeature implements Feature {
        private final ObjectMapper objectMapper;

        @Inject
        public JsonFeature(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        @Override
        public boolean configure(FeatureContext featureContext) {
            featureContext.register(new ObjectMapperProvider(objectMapper), MessageBodyReader.class, MessageBodyWriter.class);
            return true;
        }

        @Provider
        @Produces(APPLICATION_JSON)
        public static class ObjectMapperProvider extends JacksonJaxbJsonProvider {
            public ObjectMapperProvider(ObjectMapper objectMapper) {
                super();
                setMapper(objectMapper);
            }
        }
    }
}
