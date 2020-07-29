package vstocks.service.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.service.DataSourceExternalResource;
import vstocks.service.jdbc.table.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.UserSource.TWITTER;

public class JdbcUserStockServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private ActivityLogTable activityLogTable;
    private UserTable userTable;
    private MarketTable marketTable;
    private StockTable stockTable;
    private StockPriceTable stockPriceTable;
    private UserBalanceTable userBalanceTable;
    private UserStockTable userStockTable;
    private JdbcUserStockService userStockService;

    private final User user1 = new User().setId("user1").setUsername("u1").setSource(TWITTER).setDisplayName("U1");
    private final User user2 = new User().setId("user2").setUsername("u2").setSource(TWITTER).setDisplayName("U2");
    private final UserBalance userBalance1 = new UserBalance().setUserId(user1.getId()).setBalance(10);
    private final UserBalance userBalance2 = new UserBalance().setUserId(user2.getId()).setBalance(10);
    private final Market market = new Market().setId("id").setName("name");
    private final Stock stock1 = new Stock().setId("id1").setMarketId(market.getId()).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setId("id2").setMarketId(market.getId()).setSymbol("sym2").setName("name2");
    private final StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
    private final StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock2.getId()).setTimestamp(Instant.now()).setPrice(20);

    @Before
    public void setup() throws SQLException {
        activityLogTable = new ActivityLogTable();
        userTable = new UserTable();
        marketTable = new MarketTable();
        stockTable = new StockTable();
        stockPriceTable = new StockPriceTable();
        userBalanceTable = new UserBalanceTable();
        userStockTable = new UserStockTable();
        userStockService = new JdbcUserStockService(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            assertEquals(1, userBalanceTable.add(connection, userBalance1));
            assertEquals(1, userBalanceTable.add(connection, userBalance2));
            assertEquals(1, marketTable.add(connection, market));
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
            marketTable.truncate(connection);
            userBalanceTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(userStockService.get("missing-id", "missing-id", "missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        assertEquals(1, userStockService.add(userStock));

        Optional<UserStock> fetched = userStockService.get(userStock.getUserId(), userStock.getMarketId(), userStock.getStockId());
        assertTrue(fetched.isPresent());
        assertEquals(userStock.getUserId(), fetched.get().getUserId());
        assertEquals(userStock.getMarketId(), fetched.get().getMarketId());
        assertEquals(userStock.getStockId(), fetched.get().getStockId());
        assertEquals(userStock.getShares(), fetched.get().getShares());
    }

    @Test
    public void testGetForUserNone() {
        Results<UserStock> results = userStockService.getForUser(user1.getId(), new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserSome() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock2.getId()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        Results<UserStock> results = userStockService.getForUser(user1.getId(), new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(userStock1));
        assertTrue(results.getResults().contains(userStock2));
    }

    @Test
    public void testGetForStockNone() {
        Results<UserStock> results = userStockService.getForStock(stock1.getId(), new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForStockSome() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        Results<UserStock> results = userStockService.getForStock(stock1.getId(), new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(userStock1));
        assertTrue(results.getResults().contains(userStock2));
    }

    @Test
    public void testGetAllNone() {
        Results<UserStock> results = userStockService.getAll(new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSome() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        Results<UserStock> results = userStockService.getAll(new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(userStock1));
        assertTrue(results.getResults().contains(userStock2));
    }

    @Test
    public void testConsumeNone() {
        List<UserStock> list = new ArrayList<>();
        assertEquals(0, userStockService.consume(list::add));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSome() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        List<UserStock> list = new ArrayList<>();
        assertEquals(2, userStockService.consume(list::add));
        assertEquals(2, list.size());
        assertTrue(list.contains(userStock1));
        assertTrue(list.contains(userStock2));
    }

    @Test
    public void testBuyStockNoExistingUserStock() throws SQLException {
        assertEquals(1, userStockService.buyStock(user1.getId(), market.getId(), stock1.getId(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance() - stockPrice1.getPrice(), userBalance.get().getBalance());

            // Make sure user stock was created/updated
            Optional<UserStock> userStock = userStockService.get(user1.getId(), market.getId(), stock1.getId());
            assertTrue(userStock.isPresent());
            assertEquals(1, userStock.get().getShares());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getId());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(market.getId(), activityLog.getMarketId());
            assertEquals(stock1.getId(), activityLog.getStockId());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(stockPrice1.getPrice(), activityLog.getPrice());
            assertEquals(1, activityLog.getShares());
        }
    }

    @Test
    public void testBuyStockWithExistingUserStock() throws SQLException {
        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(1);
        userStockService.add(existingUserStock);

        assertEquals(1, userStockService.buyStock(user1.getId(), market.getId(), stock1.getId(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance() - stockPrice1.getPrice(), userBalance.get().getBalance());

            // Make sure user stock was created/updated
            Optional<UserStock> userStock = userStockService.get(user1.getId(), market.getId(), stock1.getId());
            assertTrue(userStock.isPresent());
            assertEquals(2, userStock.get().getShares());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getId());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(market.getId(), activityLog.getMarketId());
            assertEquals(stock1.getId(), activityLog.getStockId());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(stockPrice1.getPrice(), activityLog.getPrice());
            assertEquals(1, activityLog.getShares());
        }
    }

    @Test
    public void testBuyStockBalanceTooLow() throws SQLException {
        assertEquals(0, userStockService.buyStock(user1.getId(), market.getId(), stock1.getId(), 2));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was NOT updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance(), userBalance.get().getBalance());

            // Make sure no user stock was created/updated
            assertFalse(userStockService.get(user1.getId(), market.getId(), stock1.getId()).isPresent());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testBuyStockMissingStockPrice() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockPriceTable.delete(connection, stockPrice1.getId());
            connection.commit();
        }

        assertEquals(0, userStockService.buyStock(user1.getId(), market.getId(), stock1.getId(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was NOT updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance(), userBalance.get().getBalance());

            // Make sure no user stock was created/updated
            assertFalse(userStockService.get(user1.getId(), market.getId(), stock1.getId()).isPresent());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testSellStockNoExistingUserStock() throws SQLException {
        assertEquals(0, userStockService.sellStock(user1.getId(), market.getId(), stock1.getId(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was NOT updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance(), userBalance.get().getBalance());

            // Make sure no user stock was created/updated
            assertFalse(userStockService.get(user1.getId(), market.getId(), stock1.getId()).isPresent());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testSellStockWithExistingUserStock() throws SQLException {
        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(1);
        userStockService.add(existingUserStock);

        assertEquals(1, userStockService.sellStock(user1.getId(), market.getId(), stock1.getId(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance() + stockPrice1.getPrice(), userBalance.get().getBalance());

            // Make sure user stock was updated
            Optional<UserStock> userStock = userStockService.get(user1.getId(), market.getId(), stock1.getId());
            assertTrue(userStock.isPresent());
            assertEquals(0, userStock.get().getShares());

            // Make sure activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(1, activityLogs.getTotal());
            assertEquals(1, activityLogs.getResults().size());
            ActivityLog activityLog = activityLogs.getResults().iterator().next();
            assertNotNull(activityLog.getId());
            assertEquals(user1.getId(), activityLog.getUserId());
            assertEquals(market.getId(), activityLog.getMarketId());
            assertEquals(stock1.getId(), activityLog.getStockId());
            assertNotNull(activityLog.getTimestamp());
            assertEquals(stockPrice1.getPrice(), activityLog.getPrice());
            assertEquals(-1, activityLog.getShares());
        }
    }

    @Test
    public void testSellStockMissingStockPrice() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockPriceTable.delete(connection, stockPrice1.getId());
            connection.commit();
        }

        UserStock existingUserStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(5);
        userStockService.add(existingUserStock);

        assertEquals(0, userStockService.sellStock(user1.getId(), market.getId(), stock1.getId(), 1));

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Make sure user balance was NOT updated
            Optional<UserBalance> userBalance = userBalanceTable.get(connection, user1.getId());
            assertTrue(userBalance.isPresent());
            assertEquals(userBalance1.getBalance(), userBalance.get().getBalance());

            // Make sure the user stock was not updated
            Optional<UserStock> userStock = userStockService.get(user1.getId(), market.getId(), stock1.getId());
            assertTrue(userStock.isPresent());
            assertEquals(existingUserStock.getShares(), userStock.get().getShares());

            // Make sure no activity log was added
            Results<ActivityLog> activityLogs = activityLogTable.getForUser(connection, user1.getId(), new Page());
            assertEquals(0, activityLogs.getTotal());
            assertTrue(activityLogs.getResults().isEmpty());
        }
    }

    @Test
    public void testAdd() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        assertEquals(1, userStockService.add(userStock));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        assertEquals(1, userStockService.add(userStock));
        userStockService.add(userStock);
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userStockService.delete("missing-id", "missing-id", "missing-id"));
    }

    @Test
    public void testDelete() {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setShares(10);
        assertEquals(1, userStockService.add(userStock));
        assertEquals(1, userStockService.delete(userStock.getUserId(), userStock.getMarketId(), userStock.getStockId()));
        assertFalse(userStockService.get(userStock.getUserId(), userStock.getMarketId(), userStock.getStockId()).isPresent());
    }
}
