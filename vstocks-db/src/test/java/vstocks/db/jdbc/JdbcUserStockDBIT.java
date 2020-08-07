package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.jdbc.table.*;
import vstocks.model.*;
import vstocks.db.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.SYMBOL;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcUserStockDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private ActivityLogTable activityLogTable;
    private UserTable userTable;
    private StockTable stockTable;
    private StockPriceTable stockPriceTable;
    private UserBalanceTable userBalanceTable;
    private UserStockTable userStockTable;
    private JdbcUserStockDB userStockService;

    private final User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
    private final UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
    private final UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
    private final StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(Instant.now()).setPrice(10);
    private final StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(Instant.now()).setPrice(20);

    @Before
    public void setup() throws SQLException {
        activityLogTable = new ActivityLogTable();
        userTable = new UserTable();
        stockTable = new StockTable();
        stockPriceTable = new StockPriceTable();
        userBalanceTable = new UserBalanceTable();
        userStockTable = new UserStockTable();
        userStockService = new JdbcUserStockDB(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            assertEquals(1, userBalanceTable.add(connection, userBalance1));
            assertEquals(1, userBalanceTable.add(connection, userBalance2));
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userStockTable.truncate(connection);
            stockPriceTable.truncate(connection);
            stockTable.truncate(connection);
            userBalanceTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(userStockService.get("missing-id", TWITTER, "missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock));

        Optional<UserStock> fetched = userStockService.get(userStock.getUserId(), userStock.getMarket(), userStock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(userStock.getUserId(), fetched.get().getUserId());
        assertEquals(userStock.getMarket(), fetched.get().getMarket());
        assertEquals(userStock.getSymbol(), fetched.get().getSymbol());
        assertEquals(userStock.getShares(), fetched.get().getShares());
    }

    @Test
    public void testGetForUserNone() {
        Results<UserStock> results = userStockService.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserSomeNoSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        Results<UserStock> results = userStockService.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock1, results.getResults().get(0));
        assertEquals(userStock2, results.getResults().get(1));
    }

    @Test
    public void testGetForUserSomeWithSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), USER_ID.toSort()));
        Results<UserStock> results = userStockService.getForUser(user1.getId(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock2, results.getResults().get(0));
        assertEquals(userStock1, results.getResults().get(1));
    }

    @Test
    public void testGetForStockNone() {
        Results<UserStock> results = userStockService.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForStockSomeNoSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        Results<UserStock> results = userStockService.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock1, results.getResults().get(0));
        assertEquals(userStock2, results.getResults().get(1));
    }

    @Test
    public void testGetForStockSomeWithSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), SYMBOL.toSort()));
        Results<UserStock> results = userStockService.getForStock(TWITTER, stock1.getSymbol(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock2, results.getResults().get(0));
        assertEquals(userStock1, results.getResults().get(1));
    }

    @Test
    public void testGetAllNone() {
        Results<UserStock> results = userStockService.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        Results<UserStock> results = userStockService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock1, results.getResults().get(0));
        assertEquals(userStock2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), SYMBOL.toSort()));
        Results<UserStock> results = userStockService.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(userStock2, results.getResults().get(0));
        assertEquals(userStock1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<UserStock> list = new ArrayList<>();
        assertEquals(0, userStockService.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        List<UserStock> list = new ArrayList<>();
        assertEquals(2, userStockService.consume(list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(userStock1, list.get(0));
        assertEquals(userStock2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        List<UserStock> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), SYMBOL.toSort()));
        assertEquals(2, userStockService.consume(list::add, sort));
        assertEquals(2, list.size());
        assertEquals(userStock2, list.get(0));
        assertEquals(userStock1, list.get(1));
    }

    @Test
    public void testBuyStockZeroShares() {
        assertEquals(0, userStockService.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), 0));
    }

    @Test
    public void testBuyStockNegativeShares() {
        assertEquals(0, userStockService.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), -1));
    }

    @Test
    public void testBuyStockNoExistingUserStock() throws SQLException {
        assertEquals(1, userStockService.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance() - stockPrice1.getPrice(), userBalance.get().getBalance());

            // Make sure user stock was created/updated
            Optional<UserStock> userStock = userStockService.get(user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(userStock.isPresent());
            assertEquals(1, userStock.get().getShares());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getSymbol());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(TWITTER, activityLog.getMarket());
            assertEquals(stock1.getSymbol(), activityLog.getSymbol());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(stockPrice1.getPrice(), activityLog.getPrice());
            assertEquals(1, activityLog.getShares());
        }
    }

    @Test
    public void testBuyStockWithExistingUserStock() throws SQLException {
        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1);
        userStockService.add(existingUserStock);

        assertEquals(1, userStockService.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance() - stockPrice1.getPrice(), userBalance.get().getBalance());

            // Make sure user stock was created/updated
            Optional<UserStock> userStock = userStockService.get(user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(userStock.isPresent());
            assertEquals(2, userStock.get().getShares());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getSymbol());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(TWITTER, activityLog.getMarket());
            assertEquals(stock1.getSymbol(), activityLog.getSymbol());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(stockPrice1.getPrice(), activityLog.getPrice());
            assertEquals(1, activityLog.getShares());
        }
    }

    @Test
    public void testBuyStockBalanceTooLow() throws SQLException {
        assertEquals(0, userStockService.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), 2));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was NOT updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance(), userBalance.get().getBalance());

            // Make sure no user stock was created/updated
            assertFalse(userStockService.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testBuyStockMissingStockPrice() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockPriceTable.truncate(connection);
            connection.commit();
        }

        assertEquals(0, userStockService.buyStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was NOT updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance(), userBalance.get().getBalance());

            // Make sure no user stock was created/updated
            assertFalse(userStockService.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testSellStockZeroShares() {
        assertEquals(0, userStockService.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), 0));
    }

    @Test
    public void testSellStockNegativeShares() {
        assertEquals(0, userStockService.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), -1));
    }

    @Test
    public void testSellStockNoExistingUserStock() throws SQLException {
        assertEquals(0, userStockService.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was NOT updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance(), userBalance.get().getBalance());

            // Make sure no user stock was created/updated
            assertFalse(userStockService.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testSellStockWithExistingUserStock() throws SQLException {
        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        userStockService.add(existingUserStock);

        assertEquals(1, userStockService.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance() + stockPrice1.getPrice(), userBalance.get().getBalance());

            // Make sure user stock was updated
            Optional<UserStock> userStock = userStockService.get(user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(userStock.isPresent());
            assertEquals(9, userStock.get().getShares());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getSymbol());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(TWITTER, activityLog.getMarket());
            assertEquals(stock1.getSymbol(), activityLog.getSymbol());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(stockPrice1.getPrice(), activityLog.getPrice());
            assertEquals(-1, activityLog.getShares());
        }
    }

    @Test
    public void testSellStockWithExistingUserStockDownToZero() throws SQLException {
        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(1);
        userStockService.add(existingUserStock);

        assertEquals(1, userStockService.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance() + stockPrice1.getPrice(), userBalance.get().getBalance());

            // Make sure user stock was deleted, since the shares dropped to 0
            assertFalse(userStockService.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getSymbol());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(TWITTER, activityLog.getMarket());
            assertEquals(stock1.getSymbol(), activityLog.getSymbol());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(stockPrice1.getPrice(), activityLog.getPrice());
            assertEquals(-1, activityLog.getShares());
        }
    }

    @Test
    public void testSellStockMissingStockPrice() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockPriceTable.truncate(connection);
            connection.commit();
        }

        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(5);
        userStockService.add(existingUserStock);

        assertEquals(0, userStockService.sellStock(user1.getId(), TWITTER, stock1.getSymbol(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was NOT updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance(), userBalance.get().getBalance());

            // Make sure the user stock was not updated
            Optional<UserStock> userStock = userStockService.get(user1.getId(), TWITTER, stock1.getSymbol());
            assertTrue(userStock.isPresent());
            assertEquals(existingUserStock.getShares(), userStock.get().getShares());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page(), emptySet());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testAdd() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock));
        userStockService.add(userStock);
    }

    @Test
    public void testUpdatePositiveMissing() {
        assertEquals(1, userStockService.update(user1.getId(), TWITTER, stock1.getSymbol(), 10));

        Optional<UserStock> fetched = userStockService.get(user1.getId(), TWITTER, stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(10, fetched.get().getShares());
    }

    @Test
    public void testUpdatePositiveExisting() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock));
        assertEquals(1, userStockService.update(user1.getId(), TWITTER, stock1.getSymbol(), 10));

        Optional<UserStock> fetched = userStockService.get(user1.getId(), TWITTER, stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(20, fetched.get().getShares());
    }

    @Test
    public void testUpdateNegativeMissing() {
        assertEquals(0, userStockService.update(user1.getId(), TWITTER, stock1.getSymbol(), -10));
        // Nothing was added
        assertFalse(userStockService.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());
    }

    @Test
    public void testUpdateNegativeExistingValid() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock));
        assertEquals(1, userStockService.update(user1.getId(), TWITTER, stock1.getSymbol(), -5));

        Optional<UserStock> fetched = userStockService.get(user1.getId(), TWITTER, stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(5, fetched.get().getShares());
    }

    @Test
    public void testUpdateNegativeValidToZero() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock));
        assertEquals(1, userStockService.update(user1.getId(), TWITTER, stock1.getSymbol(), -10));
        assertFalse(userStockService.get(user1.getId(), TWITTER, stock1.getSymbol()).isPresent());
    }

    @Test
    public void testUpdateNegativeExistingInvalid() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock));
        assertEquals(0, userStockService.update(user1.getId(), TWITTER, stock1.getSymbol(), -15));

        Optional<UserStock> fetched = userStockService.get(user1.getId(), TWITTER, stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(10, fetched.get().getShares()); // Not updated
    }

    @Test
    public void testUpdateZero() {
        assertEquals(0, userStockService.update(user1.getId(), TWITTER, stock1.getSymbol(), 0));
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userStockService.delete("missing-id", TWITTER, "missing-id"));
    }

    @Test
    public void testDelete() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        assertEquals(1, userStockService.add(userStock));
        assertEquals(1, userStockService.delete(userStock.getUserId(), userStock.getMarket(), userStock.getSymbol()));
        assertFalse(userStockService.get(userStock.getUserId(), userStock.getMarket(), userStock.getSymbol()).isPresent());
    }
}
