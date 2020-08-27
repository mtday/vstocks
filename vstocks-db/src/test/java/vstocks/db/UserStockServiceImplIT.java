package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.ActivityType.STOCK_SELL;
import static vstocks.model.DatabaseField.SYMBOL;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class UserStockServiceImplIT extends BaseServiceImplIT {
    private ActivityLogService activityLogService;
    private StockPriceService stockPriceService;
    private StockService stockService;
    private UserCreditsService userCreditsService;
    private UserService userService;
    private UserStockService userStockService;

    private final User user1 = new User()
            .setId(generateId("user1@domain.com"))
            .setEmail("user1@domain.com")
            .setUsername("name1")
            .setDisplayName("Name1")
            .setProfileImage("link1");
    private final User user2 = new User()
            .setId(generateId("user2@domain.com"))
            .setEmail("user2@domain.com")
            .setUsername("name2")
            .setDisplayName("Name2")
            .setProfileImage("link2");

    private final Stock stock1 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("symbol1")
            .setName("name1")
            .setProfileImage("link1");
    private final Stock stock2 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("symbol2")
            .setName("name2")
            .setProfileImage("link2");

    private final StockPrice stockPrice1 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now)
            .setPrice(10);
    private final StockPrice stockPrice2 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now)
            .setPrice(20);

    private final UserStock userStock11 = new UserStock()
            .setUserId(user1.getId())
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setShares(11);
    private final UserStock userStock12 = new UserStock()
            .setUserId(user1.getId())
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(12);
    private final UserStock userStock21 = new UserStock()
            .setUserId(user2.getId())
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setShares(21);
    private final UserStock userStock22 = new UserStock()
            .setUserId(user2.getId())
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(22);

    @Before
    public void setup() {
        activityLogService = new ActivityLogServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userStockService = new UserStockServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));
    }

    @After
    public void cleanup() {
        userStockService.truncate();
        stockPriceService.truncate();
        stockService.truncate();
        userCreditsService.truncate();
        userService.truncate();
    }

    @Test
    public void testGetMissing() {
        assertFalse(userStockService.get("missing-id", TWITTER, "missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        assertEquals(1, userStockService.add(userStock11));

        UserStock fetched = userStockService.get(
                userStock11.getUserId(), userStock11.getMarket(), userStock11.getSymbol()).orElse(null);
        assertEquals(userStock11, fetched);
    }

    @Test
    public void testGetForUserNone() {
        Results<UserStock> results = userStockService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForUserSomeNoSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));

        Results<UserStock> results = userStockService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(results, userStock11, userStock12);
    }

    @Test
    public void testGetForUserSomeWithSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), USER_ID.toSort());
        Results<UserStock> results = userStockService.getForUser(user1.getId(), new Page(), sort);
        validateResults(results, userStock12, userStock11);
    }

    @Test
    public void testGetForStockNone() {
        Results<UserStock> results = userStockService.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForStockSomeNoSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock21));

        Results<UserStock> results = userStockService.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptyList());
        validateResults(results, userStock11, userStock21);
    }

    @Test
    public void testGetForStockSomeWithSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock21));

        List<Sort> sort = asList(USER_ID.toSort(DESC), SYMBOL.toSort());
        Results<UserStock> results = userStockService.getForStock(TWITTER, stock1.getSymbol(), new Page(), sort);
        validateResults(results, userStock21, userStock11);
    }

    @Test
    public void testGetAllNone() {
        Results<UserStock> results = userStockService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSomeNoSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock21));
        assertEquals(1, userStockService.add(userStock22));

        Results<UserStock> results = userStockService.getAll(new Page(), emptyList());
        validateResults(results, userStock11, userStock12, userStock21, userStock22);
    }

    @Test
    public void testGetAllSomeWithSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock21));
        assertEquals(1, userStockService.add(userStock22));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), USER_ID.toSort());
        Results<UserStock> results = userStockService.getAll(new Page(), sort);
        validateResults(results, userStock12, userStock22, userStock11, userStock21);
    }

    @Test
    public void testConsumeNone() {
        List<UserStock> results = new ArrayList<>();
        assertEquals(0, userStockService.consume(results::add, emptyList()));
        validateResults(results);
    }

    @Test
    public void testConsumeSomeNoSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock21));
        assertEquals(1, userStockService.add(userStock22));

        List<UserStock> results = new ArrayList<>();
        assertEquals(4, userStockService.consume(results::add, emptyList()));
        validateResults(results, userStock11, userStock12, userStock21, userStock22);
    }

    @Test
    public void testConsumeSomeWithSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock21));
        assertEquals(1, userStockService.add(userStock22));

        List<UserStock> results = new ArrayList<>();
        List<Sort> sort = asList(SYMBOL.toSort(DESC), USER_ID.toSort());
        assertEquals(4, userStockService.consume(results::add, sort));
        validateResults(results, userStock12, userStock22, userStock11, userStock21);
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
    public void testBuyStockNoExistingUserStock() {
        assertEquals(1, userStockService.buyStock(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 1));

        // Make sure user credits were updated
        UserCredits userCredits = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCredits);
        assertEquals(10000 - stockPrice1.getPrice(), userCredits.getCredits());

        // Make sure user stock was created/updated
        UserStock userStock =
                userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).orElse(null);
        assertNotNull(userStock);
        assertEquals(1, userStock.getShares());

        // Make sure activity log was added
        Results<ActivityLog> activityLogs = activityLogService.getForUser(user1.getId(), new Page(), emptyList());
        assertEquals(1, activityLogs.getTotal());
        assertEquals(1, activityLogs.getResults().size());

        ActivityLog activityLog = activityLogs.getResults().iterator().next();
        assertNotNull(activityLog.getSymbol());
        assertEquals(user1.getId(), activityLog.getUserId());
        assertEquals(STOCK_BUY, activityLog.getType());
        assertNotNull(activityLog.getTimestamp());
        assertEquals(stock1.getMarket(), activityLog.getMarket());
        assertEquals(stock1.getSymbol(), activityLog.getSymbol());
        assertEquals(stockPrice1.getPrice(), (long) activityLog.getPrice());
        assertEquals(1, (long) activityLog.getShares());
    }

    @Test
    public void testBuyStockWithExistingUserStock() {
        userStockService.add(userStock11);

        assertEquals(1, userStockService.buyStock(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 1));

        // Make sure user credits were updated
        UserCredits userCredits = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCredits);
        assertEquals(10000 - stockPrice1.getPrice(), userCredits.getCredits());

        // Make sure user stock was created/updated
        UserStock userStock =
                userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).orElse(null);
        assertNotNull(userStock);
        assertEquals(userStock11.getShares() + 1, userStock.getShares());

        // Make sure activity log was added
        Results<ActivityLog> activityLogs = activityLogService.getForUser(user1.getId(), new Page(), emptyList());
        assertEquals(1, activityLogs.getTotal());
        assertEquals(1, activityLogs.getResults().size());

        ActivityLog activityLog = activityLogs.getResults().iterator().next();
        assertNotNull(activityLog.getSymbol());
        assertEquals(user1.getId(), activityLog.getUserId());
        assertEquals(STOCK_BUY, activityLog.getType());
        assertNotNull(activityLog.getTimestamp());
        assertEquals(stock1.getMarket(), activityLog.getMarket());
        assertEquals(stock1.getSymbol(), activityLog.getSymbol());
        assertEquals(stockPrice1.getPrice(), (long) activityLog.getPrice());
        assertEquals(1, (long) activityLog.getShares());
    }

    @Test
    public void testBuyStockCreditsTooLow() {
        assertEquals(0, userStockService.buyStock(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 2000));

        // Make sure user credits were NOT updated
        UserCredits userCredits = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCredits);
        assertEquals(10000, userCredits.getCredits());

        // Make sure no user stock was created/updated
        assertFalse(userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).isPresent());

        // Make sure no activity log was added
        Results<ActivityLog> activityLogs = activityLogService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(activityLogs);
    }

    @Test
    public void testBuyStockMissingStockPrice() {
        stockPriceService.truncate();

        assertEquals(0, userStockService.buyStock(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 1));

        // Make sure user credits were NOT updated
        UserCredits userCredits = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCredits);
        assertEquals(10000, userCredits.getCredits());

        // Make sure no user stock was created/updated
        assertFalse(userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).isPresent());

        // Make sure no activity log was added
        Results<ActivityLog> activityLogs = activityLogService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(activityLogs);
    }

    @Test
    public void testSellStockZeroShares() {
        assertEquals(0, userStockService.sellStock(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 0));
    }

    @Test
    public void testSellStockNegativeShares() {
        assertEquals(0, userStockService.sellStock(user1.getId(), stock1.getMarket(), stock1.getSymbol(), -1));
    }

    @Test
    public void testSellStockNoExistingUserStock() {
        assertEquals(0, userStockService.sellStock(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 1));

        // Make sure user credits were NOT updated
        UserCredits userCredits = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCredits);
        assertEquals(10000, userCredits.getCredits());

        // Make sure no user stock was created/updated
        assertFalse(userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).isPresent());

        // Make sure no activity log was added
        Results<ActivityLog> activityLogs = activityLogService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(activityLogs);
    }

    @Test
    public void testSellStockWithExistingUserStock() {
        userStockService.add(userStock11);

        assertEquals(1, userStockService.sellStock(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 1));

        // Make sure user credits were updated
        UserCredits userCredits = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCredits);
        assertEquals(10000 + stockPrice1.getPrice(), userCredits.getCredits());

        // Make sure user stock was updated
        UserStock userStock = userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).orElse(null);
        assertNotNull(userStock);
        assertEquals(userStock11.getShares() - 1, userStock.getShares());

        // Make sure activity log was added
        Results<ActivityLog> activityLogs = activityLogService.getForUser(user1.getId(), new Page(), emptyList());
        assertEquals(1, activityLogs.getTotal());
        assertEquals(1, activityLogs.getResults().size());

        ActivityLog activityLog = activityLogs.getResults().iterator().next();
        assertNotNull(activityLog.getSymbol());
        assertEquals(user1.getId(), activityLog.getUserId());
        assertEquals(STOCK_SELL, activityLog.getType());
        assertNotNull(activityLog.getTimestamp());
        assertEquals(stock1.getMarket(), activityLog.getMarket());
        assertEquals(stock1.getSymbol(), activityLog.getSymbol());
        assertEquals(stockPrice1.getPrice(), (long) activityLog.getPrice());
        assertEquals(-1, (long) activityLog.getShares());
    }

    @Test
    public void testSellStockWithExistingUserStockDownToZero() {
        userStockService.add(userStock11);

        assertEquals(1, userStockService.sellStock(
                user1.getId(), stock1.getMarket(), stock1.getSymbol(), userStock11.getShares()));

        // Make sure user credits were updated
        UserCredits userCredits = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCredits);
        assertEquals(10000 + (stockPrice1.getPrice() * userStock11.getShares()), userCredits.getCredits());

        // Make sure user stock was deleted, since the shares dropped to 0
        assertFalse(userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).isPresent());

        // Make sure activity log was added
        Results<ActivityLog> activityLogs = activityLogService.getForUser(user1.getId(), new Page(), emptyList());
        assertEquals(1, activityLogs.getTotal());
        assertEquals(1, activityLogs.getResults().size());

        ActivityLog activityLog = activityLogs.getResults().iterator().next();
        assertNotNull(activityLog.getSymbol());
        assertEquals(user1.getId(), activityLog.getUserId());
        assertEquals(STOCK_SELL, activityLog.getType());
        assertNotNull(activityLog.getTimestamp());
        assertEquals(stock1.getMarket(), activityLog.getMarket());
        assertEquals(stock1.getSymbol(), activityLog.getSymbol());
        assertEquals(stockPrice1.getPrice(), (long) activityLog.getPrice());
        assertEquals(-userStock11.getShares(), (long) activityLog.getShares());
    }

    @Test
    public void testSellStockMissingStockPrice() {
        stockPriceService.truncate();

        userStockService.add(userStock11);

        assertEquals(0, userStockService.sellStock(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 1));

        // Make sure user credits were NOT updated
        UserCredits userCredits = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCredits);
        assertEquals(10000, userCredits.getCredits());

        // Make sure the user stock was not updated
        UserStock userStock = userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).orElse(null);
        assertEquals(userStock11, userStock);

        // Make sure no activity log was added
        Results<ActivityLog> activityLogs = activityLogService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(activityLogs);
    }

    @Test
    public void testAdd() {
        assertEquals(1, userStockService.add(userStock11));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        assertEquals(1, userStockService.add(userStock11));
        userStockService.add(userStock11);
    }

    @Test
    public void testUpdatePositiveMissing() {
        // inserts
        assertEquals(1, userStockService.update(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 10));

        UserStock fetched = userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).orElse(null);
        assertNotNull(fetched);
        assertEquals(10, fetched.getShares());
    }

    @Test
    public void testUpdatePositiveExisting() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.update(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 10));

        UserStock fetched = userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).orElse(null);
        assertNotNull(fetched);
        assertEquals(userStock11.getShares() + 10, fetched.getShares());
    }

    @Test
    public void testUpdateNegativeMissing() {
        assertEquals(0, userStockService.update(user1.getId(), stock1.getMarket(), stock1.getSymbol(), -10));
        // Nothing was added
        assertFalse(userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).isPresent());
    }

    @Test
    public void testUpdateNegativeExistingValid() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.update(user1.getId(), stock1.getMarket(), stock1.getSymbol(), -5));

        UserStock fetched = userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).orElse(null);
        assertNotNull(fetched);
        assertEquals(userStock11.getShares() - 5, fetched.getShares());
    }

    @Test
    public void testUpdateNegativeValidToZero() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.update(
                user1.getId(), stock1.getMarket(), stock1.getSymbol(), -userStock11.getShares()));

        // when number of shares hits zero, the user stock is deleted
        assertFalse(userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).isPresent());
    }

    @Test
    public void testUpdateNegativeExistingInvalid() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(0, userStockService.update(
                user1.getId(), stock1.getMarket(), stock1.getSymbol(), -userStock11.getShares() - 2));

        UserStock fetched = userStockService.get(user1.getId(), stock1.getMarket(), stock1.getSymbol()).orElse(null);
        assertNotNull(fetched);
        assertEquals(userStock11.getShares(), fetched.getShares()); // Not updated
    }

    @Test
    public void testUpdateZero() {
        assertEquals(0, userStockService.update(user1.getId(), stock1.getMarket(), stock1.getSymbol(), 0));
    }

    @Test
    public void testDeleteForUserMissing() {
        assertEquals(0, userStockService.deleteForUser("missing-id"));
    }

    @Test
    public void testDeleteForUser() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock21));
        assertEquals(1, userStockService.add(userStock22));

        assertEquals(2, userStockService.deleteForUser(user1.getId()));

        Results<UserStock> results = userStockService.getAll(new Page(), emptyList());
        validateResults(results, userStock21, userStock22);
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userStockService.delete("missing-id", stock1.getMarket(), "missing-id"));
    }

    @Test
    public void testDelete() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.delete(
                userStock11.getUserId(), userStock11.getMarket(), userStock11.getSymbol()));

        assertFalse(userStockService.get(
                userStock11.getUserId(), userStock11.getMarket(), userStock11.getSymbol()).isPresent());
    }

    @Test
    public void testTruncate() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock21));
        assertEquals(1, userStockService.add(userStock22));

        assertEquals(4, userStockService.truncate());

        Results<UserStock> results = userStockService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
