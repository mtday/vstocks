package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.ActivityType.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class ActivityLogServiceImplIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserService userService;
    private StockService stockService;
    private ActivityLogService activityLogService;

    Instant now = Instant.now().truncatedTo(SECONDS);
    private final User user1 = new User()
            .setId(generateId("user1@domain.com"))
            .setEmail("user1@domain.com")
            .setUsername("name1")
            .setDisplayName("Name1");
    private final User user2 = new User()
            .setId(generateId("user2@domain.com"))
            .setEmail("user2@domain.com")
            .setUsername("name2")
            .setDisplayName("Name2");
    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        activityLogService = new ActivityLogServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
    }

    @After
    public void cleanup() {
        activityLogService.truncate();
        stockService.truncate();
        userService.truncate();
    }

    @Test
    public void testGetMissing() {
        assertFalse(activityLogService.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog));

        ActivityLog fetched = activityLogService.get(activityLog.getId()).orElse(null);
        assertEquals(activityLog, fetched);
    }

    @Test
    public void testGetForUserNone() {
        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserSomeNoSort() {
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock2.getMarket())
                .setSymbol(stock2.getSymbol())
                .setShares(1)
                .setPrice(10L);
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
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock2.getMarket())
                .setSymbol(stock2.getSymbol())
                .setShares(1)
                .setPrice(10L);
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
    public void testGetForUserAndTypeNone() {
        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), USER_LOGIN, new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserAndTypeSomeNoSort() {
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock2.getMarket())
                .setSymbol(stock2.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), STOCK_SELL, new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog1, results.getResults().get(0));
        assertEquals(activityLog2, results.getResults().get(1));
    }

    @Test
    public void testGetForUserAndTypeSomeWithSort() {
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock2.getMarket())
                .setSymbol(stock2.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), ID.toSort()));
        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), STOCK_SELL, new Page(), sort);
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
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
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
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
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
    public void testGetForTypeNone() {
        Results<ActivityLog> results = activityLogService.getForType(STOCK_SELL, new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForTypeSomeNoSort() {
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Results<ActivityLog> results = activityLogService.getForType(STOCK_SELL, new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog1, results.getResults().get(0));
        assertEquals(activityLog2, results.getResults().get(1));
    }

    @Test
    public void testGetForTypeSomeWithSort() {
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ID.toSort()));
        Results<ActivityLog> results = activityLogService.getForType(STOCK_SELL, new Page(), sort);
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
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
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
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
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
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        List<ActivityLog> results = new ArrayList<>();
        assertEquals(2, activityLogService.consume(results::add, emptySet()));
        assertEquals(2, results.size());
        assertEquals(activityLog1, results.get(0));
        assertEquals(activityLog2, results.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        List<ActivityLog> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), ID.toSort()));
        assertEquals(2, activityLogService.consume(results::add, sort));
        assertEquals(2, results.size());
        assertEquals(activityLog2, results.get(0));
        assertEquals(activityLog1, results.get(1));
    }

    @Test
    public void testCustomConsume() {
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));

        PreparedStatementCreator psc = conn -> conn.prepareStatement("SELECT id FROM activity_logs ORDER BY id");
        RowMapper<String> mapper = rs -> rs.getString("id");
        List<String> list = new ArrayList<>();
        assertEquals(2, activityLogService.consume(psc, mapper, list::add));
        assertEquals(2, list.size());
        assertEquals("id1", list.get(0));
        assertEquals("id2", list.get(1));
    }

    @Test(expected = RuntimeException.class)
    public void testCustomConsumeInvalidSql() {
        PreparedStatementCreator psc = conn -> conn.prepareStatement("invalid");
        RowMapper<String> mapper = rs -> rs.getString("id");
        activityLogService.consume(psc, mapper, ignored -> {});
    }

    @Test
    public void testAddLogin() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(user1.getId())
                .setType(USER_LOGIN)
                .setTimestamp(now);
        assertEquals(1, activityLogService.add(activityLog));

        ActivityLog fetched = activityLogService.get(activityLog.getId()).orElse(null);
        assertEquals(activityLog, fetched);
    }

    @Test
    public void testAddPositivePrice() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog));

        ActivityLog fetched = activityLogService.get(activityLog.getId()).orElse(null);
        assertEquals(activityLog, fetched);
    }

    @Test
    public void testAddNegativePrice() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(user1.getId())
                .setType(STOCK_BUY)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(-5L);
        assertEquals(1, activityLogService.add(activityLog));

        ActivityLog fetched = activityLogService.get(activityLog.getId()).orElse(null);
        assertEquals(activityLog, fetched);
    }

    @Test
    public void testAddNegativeCreditsTooLow() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(user1.getId())
                .setType(STOCK_BUY)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(-15L);
        assertEquals(1, activityLogService.add(activityLog)); // not protected at this level

        ActivityLog fetched = activityLogService.get(activityLog.getId()).orElse(null);
        assertEquals(activityLog, fetched);
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog));
        activityLogService.add(activityLog);
    }

    @Test
    public void testDeleteForUserMissing() {
        assertEquals(0, activityLogService.deleteForUser("missing-id"));
    }

    @Test
    public void testDeleteForUser() {
        ActivityLog activityLog1 = new ActivityLog()
                .setId("id1")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog()
                .setId("id2")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock2.getMarket())
                .setSymbol(stock2.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog3 = new ActivityLog()
                .setId("id3")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        ActivityLog activityLog4 = new ActivityLog()
                .setId("id4")
                .setUserId(user2.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock2.getMarket())
                .setSymbol(stock2.getSymbol())
                .setShares(1)
                .setPrice(10L);

        assertEquals(1, activityLogService.add(activityLog1));
        assertEquals(1, activityLogService.add(activityLog2));
        assertEquals(1, activityLogService.add(activityLog3));
        assertEquals(1, activityLogService.add(activityLog4));

        assertEquals(2, activityLogService.deleteForUser(user1.getId()));

        Results<ActivityLog> results = activityLogService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(activityLog3, results.getResults().get(0));
        assertEquals(activityLog4, results.getResults().get(1));
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, activityLogService.delete("missing-id"));
    }

    @Test
    public void testDelete() {
        ActivityLog activityLog = new ActivityLog()
                .setId("id")
                .setUserId(user1.getId())
                .setType(STOCK_SELL)
                .setTimestamp(now)
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setShares(1)
                .setPrice(10L);
        assertEquals(1, activityLogService.add(activityLog));
        assertEquals(1, activityLogService.delete(activityLog.getId()));
        assertFalse(activityLogService.get(activityLog.getId()).isPresent());
    }
}
