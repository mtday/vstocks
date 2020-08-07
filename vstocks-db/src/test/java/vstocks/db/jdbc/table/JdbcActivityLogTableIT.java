package vstocks.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.SYMBOL;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcActivityLogTableIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private StockTable stockTable;
    private ActivityLogTable activityLogTable;

    private final User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
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
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
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
            assertEquals(activityLog.getTimestamp(), fetched.get().getTimestamp());
            assertEquals(activityLog.getShares(), fetched.get().getShares());
            assertEquals(activityLog.getPrice(), fetched.get().getPrice());
        }
    }

    @Test
    public void testGetForUserNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForUserSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(activityLog1, results.getResults().get(0));
            assertEquals(activityLog2, results.getResults().get(1));
        }
    }

    @Test
    public void testGetForUserSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), USER_ID.toSort()));
            Results<ActivityLog> results = activityLogTable.getForUser(connection, user1.getId(), new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(activityLog2, results.getResults().get(0));
            assertEquals(activityLog1, results.getResults().get(1));
        }
    }

    @Test
    public void testGetForStockNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getForStock(connection, TWITTER, stock1.getSymbol(), new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForStockSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getForStock(connection, TWITTER, stock1.getSymbol(), new Page(), emptySet());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(activityLog1, results.getResults().get(0));
            assertEquals(activityLog2, results.getResults().get(1));
        }
    }

    @Test
    public void testGetForStockSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), USER_ID.toSort(DESC)));
            Results<ActivityLog> results = activityLogTable.getForStock(connection, TWITTER, stock1.getSymbol(), new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(activityLog2, results.getResults().get(0));
            assertEquals(activityLog1, results.getResults().get(1));
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getAll(connection, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<ActivityLog> results = activityLogTable.getAll(connection, new Page(), emptySet());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(activityLog1, results.getResults().get(0));
            assertEquals(activityLog2, results.getResults().get(1));
        }
    }

    @Test
    public void testGetAllSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), USER_ID.toSort(DESC)));
            Results<ActivityLog> results = activityLogTable.getAll(connection, new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(activityLog2, results.getResults().get(0));
            assertEquals(activityLog1, results.getResults().get(1));
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<ActivityLog> list = new ArrayList<>();
            assertEquals(0, activityLogTable.consume(connection, list::add, emptySet()));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<ActivityLog> list = new ArrayList<>();
            assertEquals(2, activityLogTable.consume(connection, list::add, emptySet()));
            assertEquals(2, list.size());
            assertEquals(activityLog1, list.get(0));
            assertEquals(activityLog2, list.get(1));
        }
    }

    @Test
    public void testConsumeSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<ActivityLog> list = new ArrayList<>();
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), USER_ID.toSort(DESC)));
            assertEquals(2, activityLogTable.consume(connection, list::add, sort));
            assertEquals(2, list.size());
            assertEquals(activityLog2, list.get(0));
            assertEquals(activityLog1, list.get(1));
        }
    }

    @Test
    public void testAddPositive() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog));
            connection.commit();
        }
    }

    @Test
    public void testAddNegative() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(-5);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog));
            connection.commit();
        }
    }

    @Test
    public void testAddNegativeBalanceTooLow() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(-15);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog)); // not protected at this level
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
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
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
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
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog3 = new ActivityLog().setId("id3").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
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
            Results<ActivityLog> results = activityLogTable.getAll(connection, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
