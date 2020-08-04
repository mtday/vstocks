package vstocks.service.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.service.db.DataSourceExternalResource;
import vstocks.service.db.jdbc.table.ActivityLogTable;
import vstocks.service.db.jdbc.table.StockTable;
import vstocks.service.db.jdbc.table.UserTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Sort.SortDirection.DESC;
import static vstocks.model.UserSource.TwitterClient;

public class JdbcActivityLogServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private StockTable stockTable;
    private ActivityLogTable activityLogTable;
    private JdbcActivityLogService activityLogService;

    private final User user1 = new User().setId("user1").setUsername("u1").setSource(TwitterClient).setDisplayName("U1");
    private final User user2 = new User().setId("user2").setUsername("u2").setSource(TwitterClient).setDisplayName("U2");
    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        stockTable = new StockTable();
        activityLogTable = new ActivityLogTable();
        activityLogService = new JdbcActivityLogService(dataSourceExternalResource.get());

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
        assertFalse(activityLogService.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog));

        Optional<ActivityLog> fetched = activityLogService.get(activityLog.getId());
        assertTrue(fetched.isPresent());
        assertEquals(activityLog.getUserId(), fetched.get().getUserId());
        assertEquals(activityLog.getMarket(), fetched.get().getMarket());
        assertEquals(activityLog.getSymbol(), fetched.get().getSymbol());
        assertEquals(activityLog.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(activityLog.getShares(), fetched.get().getShares());
        assertEquals(activityLog.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testGetForUserNone() {
        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog1, results.getResults().get(0));
        assertEquals(activityLog2, results.getResults().get(1));
    }

    @Test
    public void testGetForUserSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), ID.toSort()));
        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog2, results.getResults().get(0));
        assertEquals(activityLog1, results.getResults().get(1));
    }

    @Test
    public void testGetForStockNone() {
        Results<ActivityLog> results = activityLogService.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForStockSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Results<ActivityLog> results = activityLogService.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog1, results.getResults().get(0));
        assertEquals(activityLog2, results.getResults().get(1));
    }

    @Test
    public void testGetForStockSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ID.toSort()));
        Results<ActivityLog> results = activityLogService.getForStock(TWITTER, stock1.getSymbol(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog2, results.getResults().get(0));
        assertEquals(activityLog1, results.getResults().get(1));
    }

    @Test
    public void testGetAllNone() {
        Results<ActivityLog> results = activityLogService.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Results<ActivityLog> results = activityLogService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog1, results.getResults().get(0));
        assertEquals(activityLog2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ID.toSort()));
        Results<ActivityLog> results = activityLogService.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog2, results.getResults().get(0));
        assertEquals(activityLog1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<ActivityLog> list = new ArrayList<>();
        assertEquals(0, activityLogService.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        List<ActivityLog> list = new ArrayList<>();
        assertEquals(2, activityLogService.consume(list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(activityLog1, list.get(0));
        assertEquals(activityLog2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        List<ActivityLog> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ID.toSort()));
        assertEquals(2, activityLogService.consume(list::add, sort));
        assertEquals(2, list.size());
        assertEquals(activityLog2, list.get(0));
        assertEquals(activityLog1, list.get(1));
    }

    @Test
    public void testAddPositive() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog));
    }

    @Test
    public void testAddNegative() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(-5);
        assertEquals(1, activityLogService.add(activityLog));
    }

    @Test
    public void testAddNegativeBalanceTooLow() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(-15);
        assertEquals(1, activityLogService.add(activityLog)); // not protected at this level
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog));
        activityLogService.add(activityLog);
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, activityLogService.delete("missing-id"));
    }

    @Test
    public void testDelete() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog));
        assertEquals(1, activityLogService.delete(activityLog.getId()));
        assertFalse(activityLogService.get(activityLog.getId()).isPresent());
    }
}
