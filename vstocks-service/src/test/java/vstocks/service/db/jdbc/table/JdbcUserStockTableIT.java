package vstocks.service.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.service.db.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.UserSource.TwitterClient;

public class JdbcUserStockTableIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private StockTable stockTable;
    private UserStockTable userStockTable;

    private final User user1 = new User().setId("user1").setUsername("u1").setSource(TwitterClient).setDisplayName("U1");
    private final User user2 = new User().setId("user2").setUsername("u2").setSource(TwitterClient).setDisplayName("U2");
    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        stockTable = new StockTable();
        userStockTable = new UserStockTable();

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userStockTable.truncate(connection);
            stockTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userStockTable.get(connection, "missing-id", TWITTER, "missing-id").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserStock> fetched = userStockTable.get(connection, userStock.getUserId(), userStock.getMarket(), userStock.getSymbol());
            assertTrue(fetched.isPresent());
            assertEquals(userStock.getUserId(), fetched.get().getUserId());
            assertEquals(userStock.getMarket(), fetched.get().getMarket());
            assertEquals(userStock.getSymbol(), fetched.get().getSymbol());
            assertEquals(userStock.getShares(), fetched.get().getShares());
        }
    }

    @Test
    public void testGetForUserNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForUserSome() throws SQLException {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(userStock1));
            assertTrue(results.getResults().contains(userStock2));
        }
    }

    @Test
    public void testGetForStockNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockTable.getForStock(connection, TWITTER, stock1.getSymbol(), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForStockSome() throws SQLException {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockTable.getForStock(connection, TWITTER, stock1.getSymbol(), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(userStock1));
            assertTrue(results.getResults().contains(userStock2));
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockTable.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(userStock1));
            assertTrue(results.getResults().contains(userStock2));
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<UserStock> list = new ArrayList<>();
            assertEquals(0, userStockTable.consume(connection, list::add));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSome() throws SQLException {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<UserStock> list = new ArrayList<>();
            assertEquals(2, userStockTable.consume(connection, list::add));
            assertEquals(2, list.size());
            assertTrue(list.contains(userStock1));
            assertTrue(list.contains(userStock2));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            userStockTable.add(connection, userStock);
        }
    }

    @Test
    public void testUpdatePositiveMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.update(connection, user1.getId(), TWITTER, stock1.getSymbol(), 10));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserStock> fetched = userStockTable.get(connection, user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(fetched.isPresent());
            assertEquals(10, fetched.get().getShares());
        }
    }

    @Test
    public void testUpdatePositiveExisting() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.update(connection, user1.getId(), TWITTER, stock1.getSymbol(), 10));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserStock> fetched = userStockTable.get(connection, user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(fetched.isPresent());
            assertEquals(20, fetched.get().getShares());
        }
    }

    @Test
    public void testUpdateNegativeMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userStockTable.update(connection, user1.getId(), TWITTER, stock1.getSymbol(), -10));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Nothing was added
            assertFalse(userStockTable.get(connection, user1.getId(), TWITTER, stock1.getSymbol()).isPresent());
        }
    }

    @Test
    public void testUpdateNegativeExistingValid() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.update(connection, user1.getId(), TWITTER, stock1.getSymbol(), -5));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserStock> fetched = userStockTable.get(connection, user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(fetched.isPresent());
            assertEquals(5, fetched.get().getShares());
        }
    }

    @Test
    public void testUpdateNegativeValidToZero() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.update(connection, user1.getId(), TWITTER, stock1.getSymbol(), -10));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userStockTable.get(connection, user1.getId(), TWITTER, stock1.getSymbol()).isPresent());
        }
    }

    @Test
    public void testUpdateNegativeExistingInvalid() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userStockTable.update(connection, user1.getId(), TWITTER, stock1.getSymbol(), -15));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<UserStock> fetched = userStockTable.get(connection, user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(fetched.isPresent());
            assertEquals(10, fetched.get().getShares()); // Not updated
        }
    }

    @Test
    public void testUpdateZero() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userStockTable.update(connection, user1.getId(), TWITTER, stock1.getSymbol(), 0));
            connection.commit();
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, userStockTable.delete(connection, "missing-id", TWITTER, "missing-id"));
            connection.commit();
        }
    }

    @Test
    public void testDelete() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.delete(connection, userStock.getUserId(), userStock.getMarket(), userStock.getSymbol()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(userStockTable.get(connection, userStock.getUserId(), userStock.getMarket(), userStock.getSymbol()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock3 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            assertEquals(1, userStockTable.add(connection, userStock3));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(3, userStockTable.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<UserStock> results = userStockTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
