package vstocks.service.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.service.DataSourceExternalResource;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.UserSource.TWITTER;

public class JdbcActivityLogStoreIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userStore;
    private MarketTable marketStore;
    private StockTable stockStore;
    private ActivityLogTable activityLogStore;

    private final User user1 = new User().setId("user1").setUsername("u1").setEmail("email1").setSource(TWITTER);
    private final User user2 = new User().setId("user2").setUsername("u2").setEmail("email2").setSource(TWITTER);
    private final Market market = new Market().setId("id").setName("name");
    private final Stock stock1 = new Stock().setId("id1").setMarketId(market.getId()).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setId("id2").setMarketId(market.getId()).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        userStore = new UserTable();
        marketStore = new MarketTable();
        stockStore = new StockTable();
        activityLogStore = new ActivityLogTable();

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStore.add(connection, user1));
            assertEquals(1, userStore.add(connection, user2));
            assertEquals(1, marketStore.add(connection, market));
            assertEquals(1, stockStore.add(connection, stock1));
            assertEquals(1, stockStore.add(connection, stock2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            activityLogStore.truncate(connection);
            stockStore.truncate(connection);
            marketStore.truncate(connection);
            userStore.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(activityLogStore.get(connection, "missing-id").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.add(connection, activityLog));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<ActivityLog> fetched = activityLogStore.get(connection, activityLog.getId());
            assertTrue(fetched.isPresent());
            assertEquals(activityLog, fetched.get());
        }
    }

    @Test
    public void testGetForUserNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogStore.getForUser(connection, user1.getId(), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForUserSome() throws SQLException {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock2.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.add(connection, activityLog1));
            assertEquals(1, activityLogStore.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogStore.getForUser(connection, user1.getId(), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(activityLog1));
            assertTrue(results.getResults().contains(activityLog2));
        }
    }

    @Test
    public void testGetForStockNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogStore.getForStock(connection, stock1.getId(), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForStockSome() throws SQLException {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.add(connection, activityLog1));
            assertEquals(1, activityLogStore.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogStore.getForStock(connection, stock1.getId(), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(activityLog1));
            assertTrue(results.getResults().contains(activityLog2));
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.add(connection, activityLog1));
            assertEquals(1, activityLogStore.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogStore.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(activityLog1));
            assertTrue(results.getResults().contains(activityLog2));
        }
    }

    @Test
    public void testAddPositive() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.add(connection, activityLog));
            connection.commit();
        }
    }

    @Test
    public void testAddNegative() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(-5);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.add(connection, activityLog));
            connection.commit();
        }
    }

    @Test
    public void testAddNegativeBalanceTooLow() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(-15);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.add(connection, activityLog)); // not protected at this level
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.add(connection, activityLog));
            activityLogStore.add(connection, activityLog);
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, activityLogStore.delete(connection, "missing-id"));
            connection.commit();
        }
    }

    @Test
    public void testDelete() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.add(connection, activityLog));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.delete(connection, activityLog.getId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(activityLogStore.get(connection, activityLog.getId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog3 = new ActivityLog().setId("id3").setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock2.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogStore.add(connection, activityLog1));
            assertEquals(1, activityLogStore.add(connection, activityLog2));
            assertEquals(1, activityLogStore.add(connection, activityLog3));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(3, activityLogStore.truncate(connection));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
