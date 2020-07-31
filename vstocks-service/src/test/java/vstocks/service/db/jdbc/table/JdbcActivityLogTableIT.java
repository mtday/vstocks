package vstocks.service.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.service.db.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.UserSource.TwitterClient;

public class JdbcActivityLogTableIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private StockTable stockTable;
    private ActivityLogTable activityLogTable;

    private final User user1 = new User().setId("user1").setUsername("u1").setSource(TwitterClient).setDisplayName("U1");
    private final User user2 = new User().setId("user2").setUsername("u2").setSource(TwitterClient).setDisplayName("U2");
    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        stockTable = new StockTable();
        activityLogTable = new ActivityLogTable();

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
            activityLogTable.truncate(connection);
            stockTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(activityLogTable.get(connection, "missing-id").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<ActivityLog> fetched = activityLogTable.get(connection, activityLog.getId());
            assertTrue(fetched.isPresent());
            assertEquals(activityLog.getUserId(), fetched.get().getUserId());
            assertEquals(activityLog.getMarket(), fetched.get().getMarket());
            assertEquals(activityLog.getSymbol(), fetched.get().getSymbol());
            assertEquals(activityLog.getTimestamp().toEpochMilli(), fetched.get().getTimestamp().toEpochMilli());
            assertEquals(activityLog.getShares(), fetched.get().getShares());
            assertEquals(activityLog.getPrice(), fetched.get().getPrice());
        }
    }

    @Test
    public void testGetForUserNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForUserSome() throws SQLException {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(activityLog1));
            assertTrue(results.getResults().contains(activityLog2));
        }
    }

    @Test
    public void testGetForStockNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getForStock(connection, TWITTER, stock1.getSymbol(), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForStockSome() throws SQLException {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getForStock(connection, TWITTER, stock1.getSymbol(), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(activityLog1));
            assertTrue(results.getResults().contains(activityLog2));
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(activityLog1));
            assertTrue(results.getResults().contains(activityLog2));
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<ActivityLog> list = new ArrayList<>();
            assertEquals(0, activityLogTable.consume(connection, list::add));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSome() throws SQLException {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<ActivityLog> list = new ArrayList<>();
            assertEquals(2, activityLogTable.consume(connection, list::add));
            assertEquals(2, list.size());
            assertTrue(list.contains(activityLog1));
            assertTrue(list.contains(activityLog2));
        }
    }

    @Test
    public void testAddPositive() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog));
            connection.commit();
        }
    }

    @Test
    public void testAddNegative() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(-5);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog));
            connection.commit();
        }
    }

    @Test
    public void testAddNegativeBalanceTooLow() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(-15);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog)); // not protected at this level
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog));
            activityLogTable.add(connection, activityLog);
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, activityLogTable.delete(connection, "missing-id"));
            connection.commit();
        }
    }

    @Test
    public void testDelete() throws SQLException {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.delete(connection, activityLog.getId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(activityLogTable.get(connection, activityLog.getId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog3 = new ActivityLog().setId("id3").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            assertEquals(1, activityLogTable.add(connection, activityLog3));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(3, activityLogTable.truncate(connection));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}