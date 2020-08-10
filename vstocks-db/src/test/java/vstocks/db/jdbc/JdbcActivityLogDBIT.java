package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.*;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.ActivityType.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcActivityLogDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private StockTable stockTable;
    private ActivityLogTable activityLogTable;
    private JdbcActivityLogDB activityLogDB;

    private final User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        stockTable = new StockTable();
        activityLogTable = new ActivityLogTable();
        activityLogDB = new JdbcActivityLogDB(dataSourceExternalResource.get());

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
    public void testGetMissing() {
        assertFalse(activityLogDB.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog));

        Optional<ActivityLog> fetched = activityLogDB.get(activityLog.getId());
        assertTrue(fetched.isPresent());
        assertEquals(activityLog.getUserId(), fetched.get().getUserId());
        assertEquals(activityLog.getType(), fetched.get().getType());
        assertEquals(activityLog.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(activityLog.getMarket(), fetched.get().getMarket());
        assertEquals(activityLog.getSymbol(), fetched.get().getSymbol());
        assertEquals(activityLog.getShares(), fetched.get().getShares());
        assertEquals(activityLog.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testGetForUserNone() {
        Results<ActivityLog> results = activityLogDB.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        Results<ActivityLog> results = activityLogDB.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog1, results.getResults().get(0));
        assertEquals(activityLog2, results.getResults().get(1));
    }

    @Test
    public void testGetForUserSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), ID.toSort()));
        Results<ActivityLog> results = activityLogDB.getForUser(user1.getId(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog2, results.getResults().get(0));
        assertEquals(activityLog1, results.getResults().get(1));
    }

    @Test
    public void testGetForUserAndTypeNone() {
        Results<ActivityLog> results = activityLogDB.getForUser(user1.getId(), USER_LOGIN, new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserAndTypeSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        Results<ActivityLog> results = activityLogDB.getForUser(user1.getId(), STOCK_SELL, new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog1, results.getResults().get(0));
        assertEquals(activityLog2, results.getResults().get(1));
    }

    @Test
    public void testGetForUserAndTypeSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), ID.toSort()));
        Results<ActivityLog> results = activityLogDB.getForUser(user1.getId(), STOCK_SELL, new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog2, results.getResults().get(0));
        assertEquals(activityLog1, results.getResults().get(1));
    }

    @Test
    public void testGetForStockNone() {
        Results<ActivityLog> results = activityLogDB.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForStockSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        Results<ActivityLog> results = activityLogDB.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog1, results.getResults().get(0));
        assertEquals(activityLog2, results.getResults().get(1));
    }

    @Test
    public void testGetForStockSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ID.toSort()));
        Results<ActivityLog> results = activityLogDB.getForStock(TWITTER, stock1.getSymbol(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog2, results.getResults().get(0));
        assertEquals(activityLog1, results.getResults().get(1));
    }

    @Test
    public void testGetForTypeNone() {
        Results<ActivityLog> results = activityLogDB.getForType(STOCK_SELL, new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForTypeSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        Results<ActivityLog> results = activityLogDB.getForType(STOCK_SELL, new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog1, results.getResults().get(0));
        assertEquals(activityLog2, results.getResults().get(1));
    }

    @Test
    public void testGetForTypeSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ID.toSort()));
        Results<ActivityLog> results = activityLogDB.getForType(STOCK_SELL, new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog2, results.getResults().get(0));
        assertEquals(activityLog1, results.getResults().get(1));
    }

    @Test
    public void testGetAllNone() {
        Results<ActivityLog> results = activityLogDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        Results<ActivityLog> results = activityLogDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog1, results.getResults().get(0));
        assertEquals(activityLog2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ID.toSort()));
        Results<ActivityLog> results = activityLogDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog2, results.getResults().get(0));
        assertEquals(activityLog1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<ActivityLog> list = new ArrayList<>();
        assertEquals(0, activityLogDB.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        List<ActivityLog> list = new ArrayList<>();
        assertEquals(2, activityLogDB.consume(list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(activityLog1, list.get(0));
        assertEquals(activityLog2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        List<ActivityLog> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ID.toSort()));
        assertEquals(2, activityLogDB.consume(list::add, sort));
        assertEquals(2, list.size());
        assertEquals(activityLog2, list.get(0));
        assertEquals(activityLog1, list.get(1));
    }

    @Test
    public void testCustomConsume() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));

        PreparedStatementCreator psc = conn -> conn.prepareStatement("SELECT id FROM activity_logs ORDER BY id");
        RowMapper<String> mapper = rs -> rs.getString("id");
        List<String> list = new ArrayList<>();
        assertEquals(2, activityLogDB.consume(psc, mapper, list::add));
        assertEquals(2, list.size());
        assertEquals("id1", list.get(0));
        assertEquals("id2", list.get(1));
    }

    @Test(expected = RuntimeException.class)
    public void testCustomConsumeInvalidSql() {
        PreparedStatementCreator psc = conn -> conn.prepareStatement("invalid");
        RowMapper<String> mapper = rs -> rs.getString("id");
        activityLogDB.consume(psc, mapper, ignored -> {});
    }

    @Test
    public void testAddLogin() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setType(USER_LOGIN).setTimestamp(now);
        assertEquals(1, activityLogDB.add(activityLog));

        Optional<ActivityLog> fetched = activityLogDB.get(activityLog.getId());
        assertTrue(fetched.isPresent());
        assertEquals(activityLog.getUserId(), fetched.get().getUserId());
        assertEquals(activityLog.getType(), fetched.get().getType());
        assertEquals(activityLog.getTimestamp(), fetched.get().getTimestamp());
        assertNull(fetched.get().getMarket());
        assertNull(fetched.get().getSymbol());
        assertNull(fetched.get().getShares());
        assertNull(fetched.get().getPrice());
    }

    @Test
    public void testAddPositivePrice() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog));

        Optional<ActivityLog> fetched = activityLogDB.get(activityLog.getId());
        assertTrue(fetched.isPresent());
        assertEquals(activityLog.getUserId(), fetched.get().getUserId());
        assertEquals(activityLog.getType(), fetched.get().getType());
        assertEquals(activityLog.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(activityLog.getMarket(), fetched.get().getMarket());
        assertEquals(activityLog.getSymbol(), fetched.get().getSymbol());
        assertEquals(activityLog.getShares(), fetched.get().getShares());
        assertEquals(activityLog.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testAddNegativePrice() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setType(STOCK_BUY).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(-5);
        assertEquals(1, activityLogDB.add(activityLog));

        Optional<ActivityLog> fetched = activityLogDB.get(activityLog.getId());
        assertTrue(fetched.isPresent());
        assertEquals(activityLog.getUserId(), fetched.get().getUserId());
        assertEquals(activityLog.getType(), fetched.get().getType());
        assertEquals(activityLog.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(activityLog.getMarket(), fetched.get().getMarket());
        assertEquals(activityLog.getSymbol(), fetched.get().getSymbol());
        assertEquals(activityLog.getShares(), fetched.get().getShares());
        assertEquals(activityLog.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testAddNegativeCreditsTooLow() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setType(STOCK_BUY).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(-15);
        assertEquals(1, activityLogDB.add(activityLog)); // not protected at this level

        Optional<ActivityLog> fetched = activityLogDB.get(activityLog.getId());
        assertTrue(fetched.isPresent());
        assertEquals(activityLog.getUserId(), fetched.get().getUserId());
        assertEquals(activityLog.getType(), fetched.get().getType());
        assertEquals(activityLog.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(activityLog.getMarket(), fetched.get().getMarket());
        assertEquals(activityLog.getSymbol(), fetched.get().getSymbol());
        assertEquals(activityLog.getShares(), fetched.get().getShares());
        assertEquals(activityLog.getPrice(), fetched.get().getPrice());
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog));
        activityLogDB.add(activityLog);
    }

    @Test
    public void testDeleteForUserMissing() {
        assertEquals(0, activityLogDB.deleteForUser("missing-id"));
    }

    @Test
    public void testDeleteForUser() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog3 = new ActivityLog().setId("id3").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        ActivityLog activityLog4 = new ActivityLog().setId("id4").setUserId(user2.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(1).setPrice(10);

        assertEquals(1, activityLogDB.add(activityLog1));
        assertEquals(1, activityLogDB.add(activityLog2));
        assertEquals(1, activityLogDB.add(activityLog3));
        assertEquals(1, activityLogDB.add(activityLog4));

        assertEquals(2, activityLogDB.deleteForUser(user1.getId()));

        Results<ActivityLog> results = activityLogDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog3, results.getResults().get(0));
        assertEquals(activityLog4, results.getResults().get(1));
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, activityLogDB.delete("missing-id"));
    }

    @Test
    public void testDelete() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setType(STOCK_SELL).setTimestamp(now).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1).setPrice(10);
        assertEquals(1, activityLogDB.add(activityLog));
        assertEquals(1, activityLogDB.delete(activityLog.getId()));
        assertFalse(activityLogDB.get(activityLog.getId()).isPresent());
    }
}
