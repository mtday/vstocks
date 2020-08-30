package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static vstocks.model.ActivityType.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class ActivityLogServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private StockService stockService;
    private ActivityLogService activityLogService;

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

    private final Stock stock1 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("sym1")
            .setName("name1")
            .setProfileImage("link1");
    private final Stock stock2 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("sym2")
            .setName("name2")
            .setProfileImage("link2");
    private final Stock stock3 = new Stock()
            .setMarket(YOUTUBE)
            .setSymbol("sym3")
            .setName("name3")
            .setProfileImage("link3");

    private final ActivityLog activityLog11 = new ActivityLog()
            .setId("id11")
            .setUserId(user1.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setShares(1L)
            .setPrice(10L)
            .setValue(10L);
    private final ActivityLog activityLog12 = new ActivityLog()
            .setId("id12")
            .setUserId(user1.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(1L)
            .setPrice(10L)
            .setValue(10L);
    private final ActivityLog activityLog13 = new ActivityLog()
            .setId("id13")
            .setUserId(user1.getId())
            .setType(STOCK_BUY)
            .setTimestamp(now)
            .setMarket(stock3.getMarket())
            .setSymbol(stock3.getSymbol())
            .setShares(2L)
            .setPrice(20L)
            .setValue(40L);
    private final ActivityLog activityLog21 = new ActivityLog()
            .setId("id21")
            .setUserId(user2.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setShares(1L)
            .setPrice(10L)
            .setValue(10L);
    private final ActivityLog activityLog22 = new ActivityLog()
            .setId("id22")
            .setUserId(user2.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(1L)
            .setPrice(10L)
            .setValue(10L);
    private final ActivityLog activityLog23 = new ActivityLog()
            .setId("id23")
            .setUserId(user2.getId())
            .setType(STOCK_BUY)
            .setTimestamp(now)
            .setMarket(stock3.getMarket())
            .setSymbol(stock3.getSymbol())
            .setShares(2L)
            .setPrice(20L)
            .setValue(40L);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        activityLogService = new ActivityLogServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));
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
        assertEquals(1, activityLogService.add(activityLog11));

        ActivityLog fetched = activityLogService.get(activityLog11.getId()).orElse(null);
        assertEquals(activityLog11, fetched);
    }

    @Test
    public void testGetForUserNone() {
        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForUserSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));

        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(results, activityLog11, activityLog12, activityLog13);
    }

    @Test
    public void testGetForUserSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), ID.toSort());
        Results<ActivityLog> results = activityLogService.getForUser(user1.getId(), new Page(), sort);
        validateResults(results, activityLog13, activityLog12, activityLog11);
    }

    @Test
    public void testGetForUserAndTypeNone() {
        Results<ActivityLog> results =
                activityLogService.getForUser(user1.getId(), singleton(USER_LOGIN), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForUserAndSingleTypeSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));

        Results<ActivityLog> results =
                activityLogService.getForUser(user1.getId(), singleton(STOCK_SELL), new Page(), emptyList());
        validateResults(results, activityLog11, activityLog12);
    }

    @Test
    public void testGetForUserAndSingleTypeSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), ID.toSort());
        Results<ActivityLog> results =
                activityLogService.getForUser(user1.getId(), singleton(STOCK_SELL), new Page(), sort);
        validateResults(results, activityLog12, activityLog11);
    }

    @Test
    public void testGetForUserAndMarketAndTypeNone() {
        Results<ActivityLog> results =
                activityLogService.getForUser(user1.getId(), TWITTER, singleton(USER_LOGIN), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForUserAndMarketAndSingleTypeSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));

        Results<ActivityLog> results =
                activityLogService.getForUser(user1.getId(), TWITTER, singleton(STOCK_SELL), new Page(), emptyList());
        validateResults(results, activityLog11, activityLog12);
    }

    @Test
    public void testGetForUserAndMarketAndSingleTypeSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), ID.toSort());
        Results<ActivityLog> results =
                activityLogService.getForUser(user1.getId(), TWITTER, singleton(STOCK_SELL), new Page(), sort);
        validateResults(results, activityLog12, activityLog11);
    }

    @Test
    public void testGetForUserAndMultipleTypeSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));

        Results<ActivityLog> results =
                activityLogService.getForUser(user1.getId(), Set.of(STOCK_BUY, STOCK_SELL), new Page(), emptyList());
        validateResults(results, activityLog11, activityLog12, activityLog13);
    }

    @Test
    public void testGetForUserAndMultipleTypeSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), ID.toSort());
        Results<ActivityLog> results =
                activityLogService.getForUser(user1.getId(), Set.of(STOCK_BUY, STOCK_SELL), new Page(), sort);
        validateResults(results, activityLog13, activityLog12, activityLog11);
    }

    @Test
    public void testGetForStockNone() {
        Results<ActivityLog> results =
                activityLogService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForStockSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog23));

        Results<ActivityLog> results =
                activityLogService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), emptyList());
        validateResults(results, activityLog11, activityLog21);
    }

    @Test
    public void testGetForStockSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog23));

        List<Sort> sort = asList(USER_ID.toSort(DESC), ID.toSort());
        Results<ActivityLog> results =
                activityLogService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), sort);
        validateResults(results, activityLog21, activityLog11);
    }

    @Test
    public void testGetForTypeNone() {
        Results<ActivityLog> results = activityLogService.getForType(STOCK_SELL, new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForTypeSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog13));

        Results<ActivityLog> results = activityLogService.getForType(STOCK_SELL, new Page(), emptyList());
        validateResults(results, activityLog11, activityLog21);
    }

    @Test
    public void testGetForTypeSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog13));

        List<Sort> sort = asList(USER_ID.toSort(DESC), ID.toSort());
        Results<ActivityLog> results = activityLogService.getForType(STOCK_SELL, new Page(), sort);
        validateResults(results, activityLog21, activityLog11);
    }

    @Test
    public void testGetAllNone() {
        Results<ActivityLog> results = activityLogService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));
        assertEquals(1, activityLogService.add(activityLog23));

        Results<ActivityLog> results = activityLogService.getAll(new Page(), emptyList());
        validateResults(results, activityLog11, activityLog12, activityLog13, activityLog21, activityLog22, activityLog23);
    }

    @Test
    public void testGetAllSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));
        assertEquals(1, activityLogService.add(activityLog23));

        List<Sort> sort = asList(VALUE.toSort(DESC), ID.toSort());
        Results<ActivityLog> results = activityLogService.getAll(new Page(), sort);
        validateResults(results, activityLog13, activityLog23, activityLog11, activityLog12, activityLog21, activityLog22);
    }

    @Test
    public void testConsumeNone() {
        List<ActivityLog> results = new ArrayList<>();
        assertEquals(0, activityLogService.consume(results::add, emptyList()));
        validateResults(results);
    }

    @Test
    public void testConsumeSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog21));

        List<ActivityLog> results = new ArrayList<>();
        assertEquals(2, activityLogService.consume(results::add, emptyList()));
        validateResults(results, activityLog11, activityLog21);
    }

    @Test
    public void testConsumeSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog21));

        List<ActivityLog> results = new ArrayList<>();
        List<Sort> sort = asList(USER_ID.toSort(DESC), ID.toSort());
        assertEquals(2, activityLogService.consume(results::add, sort));
        validateResults(results, activityLog21, activityLog11);
    }

    @Test
    public void testCustomConsume() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog21));

        PreparedStatementCreator psc = conn -> conn.prepareStatement("SELECT id FROM activity_logs ORDER BY id");
        RowMapper<String> mapper = rs -> rs.getString("id");
        List<String> results = new ArrayList<>();
        assertEquals(2, activityLogService.consume(psc, mapper, results::add));
        validateResults(results, activityLog11.getId(), activityLog21.getId());
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
        assertEquals(1, activityLogService.add(activityLog11));

        ActivityLog fetched = activityLogService.get(activityLog11.getId()).orElse(null);
        assertEquals(activityLog11, fetched);
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
                .setShares(1L)
                .setPrice(-5L)
                .setValue(-5L);
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
                .setShares(1L)
                .setPrice(-15L)
                .setValue(-15L);
        assertEquals(1, activityLogService.add(activityLog)); // not protected at this level

        ActivityLog fetched = activityLogService.get(activityLog.getId()).orElse(null);
        assertEquals(activityLog, fetched);
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        assertEquals(1, activityLogService.add(activityLog11));
        activityLogService.add(activityLog11);
    }

    @Test
    public void testDeleteForUserMissing() {
        assertEquals(0, activityLogService.deleteForUser("missing-id"));
    }

    @Test
    public void testDeleteForUser() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));

        assertEquals(2, activityLogService.deleteForUser(user1.getId()));

        Results<ActivityLog> results = activityLogService.getAll(new Page(), emptyList());
        validateResults(results, activityLog21, activityLog22);
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, activityLogService.delete("missing-id"));
    }

    @Test
    public void testDelete() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.delete(activityLog11.getId()));
        assertFalse(activityLogService.get(activityLog11.getId()).isPresent());
    }
}
