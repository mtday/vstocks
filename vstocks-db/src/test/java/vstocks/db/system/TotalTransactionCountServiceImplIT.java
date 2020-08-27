package vstocks.db.system;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.system.TotalTransactionCount;
import vstocks.model.system.TotalTransactionCountCollection;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.ActivityType.STOCK_SELL;
import static vstocks.model.DatabaseField.COUNT;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class TotalTransactionCountServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private StockService stockService;
    private ActivityLogService activityLogService;
    private TotalTransactionCountService totalTransactionCountService;

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

    private final ActivityLog activityLog11 = new ActivityLog()
            .setId("id11")
            .setUserId(user1.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setShares(1)
            .setPrice(10L);
    private final ActivityLog activityLog12 = new ActivityLog()
            .setId("id12")
            .setUserId(user1.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(1)
            .setPrice(10L);
    private final ActivityLog activityLog21 = new ActivityLog()
            .setId("id21")
            .setUserId(user2.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setShares(1)
            .setPrice(10L);
    private final ActivityLog activityLog22 = new ActivityLog()
            .setId("id22")
            .setUserId(user2.getId())
            .setType(STOCK_SELL)
            .setTimestamp(now)
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(1)
            .setPrice(10L);

    private final TotalTransactionCount totalTransactionCount1 = new TotalTransactionCount()
            .setTimestamp(now)
            .setCount(1);
    private final TotalTransactionCount totalTransactionCount2 = new TotalTransactionCount()
            .setTimestamp(now.minusSeconds(10))
            .setCount(2);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        activityLogService = new ActivityLogServiceImpl(dataSourceExternalResource.get());
        totalTransactionCountService = new TotalTransactionCountServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
    }

    @After
    public void cleanup() {
        totalTransactionCountService.truncate();
        activityLogService.truncate();
        stockService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerateEmpty() {
        assertEquals(1, totalTransactionCountService.generate());

        Results<TotalTransactionCount> results = totalTransactionCountService.getAll(new Page(), emptyList());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(0, results.getResults().iterator().next().getCount());
    }

    @Test
    public void testGenerateSomeActivity() {
        assertEquals(1, activityLogService.add(activityLog11));
        assertEquals(1, activityLogService.add(activityLog12));
        assertEquals(1, activityLogService.add(activityLog21));
        assertEquals(1, activityLogService.add(activityLog22));

        assertEquals(1, totalTransactionCountService.generate());

        Results<TotalTransactionCount> results = totalTransactionCountService.getAll(new Page(), emptyList());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(4, results.getResults().iterator().next().getCount());
    }

    @Test
    public void testGetLatestNone() {
        TotalTransactionCountCollection latest = totalTransactionCountService.getLatest();
        assertTrue(latest.getCounts().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount1));
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount2));

        TotalTransactionCountCollection latest = totalTransactionCountService.getLatest();
        validateResults(latest.getCounts(), totalTransactionCount1, totalTransactionCount2);
        assertEquals(getDeltas(2L, 1L, -1, -50f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<TotalTransactionCount> results = totalTransactionCountService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount1));
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount2));

        Results<TotalTransactionCount> results = totalTransactionCountService.getAll(new Page(), emptyList());
        validateResults(results, totalTransactionCount1, totalTransactionCount2);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount1));
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount2));

        List<Sort> sort = asList(COUNT.toSort(DESC), TIMESTAMP.toSort(DESC));
        Results<TotalTransactionCount> results = totalTransactionCountService.getAll(new Page(), sort);
        validateResults(results, totalTransactionCount2, totalTransactionCount1);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount1));
        assertEquals(0, totalTransactionCountService.add(totalTransactionCount1));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount1));
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount2));

        totalTransactionCountService.ageOff(now.minusSeconds(5));

        Results<TotalTransactionCount> results = totalTransactionCountService.getAll(new Page(), emptyList());
        validateResults(results, totalTransactionCount1);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount1));
        assertEquals(1, totalTransactionCountService.add(totalTransactionCount2));

        totalTransactionCountService.truncate();

        Results<TotalTransactionCount> results = totalTransactionCountService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
