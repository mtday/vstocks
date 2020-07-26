package vstocks.db.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.store.impl.JdbcActivityLogStore;
import vstocks.db.store.impl.JdbcMarketStore;
import vstocks.db.store.impl.JdbcStockStore;
import vstocks.db.store.impl.JdbcUserStore;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.UserSource.TWITTER;

public class DefaultActivityLogServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private JdbcUserStore userStore;
    private JdbcMarketStore marketStore;
    private JdbcStockStore stockStore;
    private JdbcActivityLogStore activityLogStore;
    private DefaultActivityLogService activityLogService;

    private final User user1 = new User().setId("user1").setUsername("u1").setEmail("email1").setSource(TWITTER);
    private final User user2 = new User().setId("user2").setUsername("u2").setEmail("email2").setSource(TWITTER);
    private final Market market = new Market().setId("id").setName("name");
    private final Stock stock1 = new Stock().setId("id1").setMarketId(market.getId()).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setId("id2").setMarketId(market.getId()).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        userStore = new JdbcUserStore();
        marketStore = new JdbcMarketStore();
        stockStore = new JdbcStockStore();
        activityLogStore = new JdbcActivityLogStore();
        activityLogService = new DefaultActivityLogService(dataSourceExternalResource.get(), activityLogStore);

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
    public void testGetMissing() {
        assertFalse(activityLogService.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog));

        Optional<ActivityLog> fetched = activityLogService.get(activityLog.getId());
        assertTrue(fetched.isPresent());
        assertEquals(activityLog, fetched.get());
    }

    @Test
    public void testGetForUserNone() {
        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserSome() {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock2.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(activityLog1));
        assertTrue(results.getResults().contains(activityLog2));
    }

    @Test
    public void testGetForStockNone() {
        Results<ActivityLog> results = activityLogService.getForStock(stock1.getId(), new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForStockSome() {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Results<ActivityLog> results = activityLogService.getForStock(stock1.getId(), new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(activityLog1));
        assertTrue(results.getResults().contains(activityLog2));
    }

    @Test
    public void testGetAllNone() {
        Results<ActivityLog> results = activityLogService.getAll(new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSome() {
        ActivityLog activityLog1 = new ActivityLog().setId("id1").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        ActivityLog activityLog2 = new ActivityLog().setId("id2").setUserId(user2.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Results<ActivityLog> results = activityLogService.getAll(new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(activityLog1));
        assertTrue(results.getResults().contains(activityLog2));
    }

    @Test
    public void testAddPositive() {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog));
    }

    @Test
    public void testAddNegative() {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(-5);
        assertEquals(1, activityLogService.add(activityLog));
    }

    @Test
    public void testAddNegativeBalanceTooLow() {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(-15);
        assertEquals(1, activityLogService.add(activityLog)); // not protected at this level
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog));
        activityLogService.add(activityLog);
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, activityLogService.delete("missing-id"));
    }

    @Test
    public void testDelete() {
        ActivityLog activityLog = new ActivityLog().setId("id").setUserId(user1.getId()).setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setShares(1).setPrice(10);
        assertEquals(1, activityLogService.add(activityLog));
        assertEquals(1, activityLogService.delete(activityLog.getId()));
        assertFalse(activityLogService.get(activityLog.getId()).isPresent());
    }
}
