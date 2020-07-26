package vstocks.service.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.service.DataSourceExternalResource;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.UserSource.TWITTER;

public class JdbcUserStockStoreIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userStore;
    private MarketTable marketStore;
    private StockTable stockStore;
    private UserStockTable userStockStore;

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
        userStockStore = new UserStockTable();

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
            userStockStore.truncate(connection);
            stockStore.truncate(connection);
            marketStore.truncate(connection);
            userStore.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userStockStore.get(connection, "missing-id", "missing-id", "missing-id").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockStore.add(connection, userStock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserStock> fetched = userStockStore.get(connection, userStock.getUserId(), userStock.getMarketId(), userStock.getStockId());
            assertTrue(fetched.isPresent());
            assertEquals(userStock, fetched.get());
        }
    }

    @Test
    public void testGetForUserNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockStore.getForUser(connection, user1.getId(), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForUserSome() throws SQLException {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock2.getId()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockStore.add(connection, userStock1));
            assertEquals(1, userStockStore.add(connection, userStock2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockStore.getForUser(connection, user1.getId(), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(userStock1));
            assertTrue(results.getResults().contains(userStock2));
        }
    }

    @Test
    public void testGetForStockNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockStore.getForStock(connection, stock1.getId(), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForStockSome() throws SQLException {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockStore.add(connection, userStock1));
            assertEquals(1, userStockStore.add(connection, userStock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockStore.getForStock(connection, stock1.getId(), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(userStock1));
            assertTrue(results.getResults().contains(userStock2));
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockStore.add(connection, userStock1));
            assertEquals(1, userStockStore.add(connection, userStock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockStore.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(userStock1));
            assertTrue(results.getResults().contains(userStock2));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockStore.add(connection, userStock));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockStore.add(connection, userStock));
            userStockStore.add(connection, userStock);
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userStockStore.delete(connection, "missing-id", "missing-id", "missing-id"));
            connection.commit();
        }
    }

    @Test
    public void testDelete() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockStore.add(connection, userStock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockStore.delete(connection, userStock.getUserId(), userStock.getMarketId(), userStock.getStockId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userStockStore.get(connection, userStock.getUserId(), userStock.getMarketId(), userStock.getStockId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        UserStock userStock3 = new UserStock().setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock2.getId()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockStore.add(connection, userStock1));
            assertEquals(1, userStockStore.add(connection, userStock2));
            assertEquals(1, userStockStore.add(connection, userStock3));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(3, userStockStore.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
