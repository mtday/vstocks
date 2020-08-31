package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.*;

import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static vstocks.model.ActivityType.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class StockActivityLogServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private StockService stockService;
    private ActivityLogService activityLogService;
    private StockActivityLogService stockActivityLogService;

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

    private final StockActivityLog stockActivityLog11 = new StockActivityLog()
            .setId("id11")
            .setUserId(user1.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setName(stock1.getName())
            .setProfileImage(stock1.getProfileImage())
            .setShares(1L)
            .setPrice(10L)
            .setValue(10L);
    private final StockActivityLog stockActivityLog12 = new StockActivityLog()
            .setId("id12")
            .setUserId(user1.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setName(stock2.getName())
            .setProfileImage(stock2.getProfileImage())
            .setShares(1L)
            .setPrice(10L)
            .setValue(10L);
    private final StockActivityLog stockActivityLog13 = new StockActivityLog()
            .setId("id13")
            .setUserId(user1.getId())
            .setType(STOCK_BUY)
            .setTimestamp(now)
            .setMarket(stock3.getMarket())
            .setSymbol(stock3.getSymbol())
            .setName(stock3.getName())
            .setProfileImage(stock3.getProfileImage())
            .setShares(2L)
            .setPrice(20L)
            .setValue(40L);
    private final StockActivityLog stockActivityLog21 = new StockActivityLog()
            .setId("id21")
            .setUserId(user2.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setName(stock1.getName())
            .setProfileImage(stock1.getProfileImage())
            .setShares(2L)
            .setShares(1L)
            .setPrice(10L)
            .setValue(10L);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        activityLogService = new ActivityLogServiceImpl(dataSourceExternalResource.get());
        stockActivityLogService = new StockActivityLogServiceImpl(dataSourceExternalResource.get());

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
    public void testGetForUserAndTypeNone() {
        Results<StockActivityLog> results =
                stockActivityLogService.getForUser(user1.getId(), singleton(USER_LOGIN), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForUserAndSingleTypeSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));

        Results<StockActivityLog> results =
                stockActivityLogService.getForUser(user1.getId(), singleton(STOCK_SELL), new Page(), emptyList());
        validateResults(results, stockActivityLog11, stockActivityLog12);
    }

    @Test
    public void testGetForUserAndSingleTypeSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));
        assertEquals(1, activityLogService.add(activityLog23));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), ID.toSort());
        Results<StockActivityLog> results =
                stockActivityLogService.getForUser(user1.getId(), singleton(STOCK_SELL), new Page(), sort);
        validateResults(results, stockActivityLog12, stockActivityLog11);
    }

    @Test
    public void testGetForUserAndMarketAndTypeNone() {
        Results<StockActivityLog> results = stockActivityLogService.getForUser(
                user1.getId(), TWITTER, singleton(USER_LOGIN), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForUserAndMarketAndSingleTypeSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));
        assertEquals(1, activityLogService.add(activityLog23));

        Results<StockActivityLog> results = stockActivityLogService.getForUser(
                user1.getId(), TWITTER, singleton(STOCK_SELL), new Page(), emptyList());
        validateResults(results, stockActivityLog11, stockActivityLog12);
    }

    @Test
    public void testGetForUserAndMarketAndSingleTypeSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));
        assertEquals(1, activityLogService.add(activityLog23));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), ID.toSort());
        Results<StockActivityLog> results =
                stockActivityLogService.getForUser(user1.getId(), TWITTER, singleton(STOCK_SELL), new Page(), sort);
        validateResults(results, stockActivityLog12, stockActivityLog11);
    }

    @Test
    public void testGetForUserAndMultipleTypeSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));
        assertEquals(1, activityLogService.add(activityLog23));

        Results<StockActivityLog> results = stockActivityLogService.getForUser(
                user1.getId(), Set.of(STOCK_BUY, STOCK_SELL), new Page(), emptyList());
        validateResults(results, stockActivityLog11, stockActivityLog12, stockActivityLog13);
    }

    @Test
    public void testGetForUserAndMultipleTypeSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));
        assertEquals(1, activityLogService.add(activityLog23));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), ID.toSort());
        Results<StockActivityLog> results =
                stockActivityLogService.getForUser(user1.getId(), Set.of(STOCK_BUY, STOCK_SELL), new Page(), sort);
        validateResults(results, stockActivityLog13, stockActivityLog12, stockActivityLog11);
    }

    @Test
    public void testGetForStockNone() {
        Results<StockActivityLog> results =
                stockActivityLogService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForStockSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));
        assertEquals(1, activityLogService.add(activityLog23));

        Results<StockActivityLog> results =
                stockActivityLogService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), emptyList());
        validateResults(results, stockActivityLog11, stockActivityLog21);
    }

    @Test
    public void testGetForStockSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog13));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));
        assertEquals(1, activityLogService.add(activityLog23));

        List<Sort> sort = asList(USER_ID.toSort(DESC), ID.toSort());
        Results<StockActivityLog> results =
                stockActivityLogService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), sort);
        validateResults(results, stockActivityLog21, stockActivityLog11);
    }

    @Test
    public void testGetForTypeNone() {
        Results<StockActivityLog> results = stockActivityLogService.getForType(STOCK_SELL, new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForTypeSomeNoSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog13));

        Results<StockActivityLog> results = stockActivityLogService.getForType(STOCK_SELL, new Page(), emptyList());
        validateResults(results, stockActivityLog11, stockActivityLog21);
    }

    @Test
    public void testGetForTypeSomeWithSort() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog13));

        List<Sort> sort = asList(USER_ID.toSort(DESC), ID.toSort());
        Results<StockActivityLog> results = stockActivityLogService.getForType(STOCK_SELL, new Page(), sort);
        validateResults(results, stockActivityLog21, stockActivityLog11);
    }
}
