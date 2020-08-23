package vstocks.db;

import junit.framework.AssertionFailedError;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.*;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class PricedUserStockServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private UserStockService userStockService;
    private StockService stockService;
    private StockPriceService stockPriceService;
    private PricedUserStockService pricedUserStockService;

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
            .setProfileImage("link");
    private final Stock stock2 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("sym2")
            .setName("name2")
            .setProfileImage("link");

    private final StockPrice stockPrice11 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now)
            .setPrice(10);
    private final StockPrice stockPrice12 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(8);
    private final StockPrice stockPrice21 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now)
            .setPrice(10);
    private final StockPrice stockPrice22 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(12);

    private final UserStock userStock11 = new UserStock()
            .setUserId(user1.getId())
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setShares(10);
    private final UserStock userStock12 = new UserStock()
            .setUserId(user1.getId())
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(10);
    private final UserStock userStock21 = new UserStock()
            .setUserId(user2.getId())
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setShares(10);
    private final UserStock userStock22 = new UserStock()
            .setUserId(user2.getId())
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(10);

    private final PricedUserStock pricedUserStock11 = new PricedUserStock()
            .setUserId(userStock11.getUserId())
            .setMarket(userStock11.getMarket())
            .setSymbol(userStock11.getSymbol())
            .setShares(userStock11.getShares())
            .setTimestamp(stockPrice11.getTimestamp())
            .setPrice(stockPrice11.getPrice());
    private final PricedUserStock pricedUserStock12 = new PricedUserStock()
            .setUserId(userStock12.getUserId())
            .setMarket(userStock12.getMarket())
            .setSymbol(userStock12.getSymbol())
            .setShares(userStock12.getShares())
            .setTimestamp(stockPrice21.getTimestamp())
            .setPrice(stockPrice21.getPrice());
    private final PricedUserStock pricedUserStock21 = new PricedUserStock()
            .setUserId(userStock21.getUserId())
            .setMarket(userStock21.getMarket())
            .setSymbol(userStock21.getSymbol())
            .setShares(userStock21.getShares())
            .setTimestamp(stockPrice11.getTimestamp())
            .setPrice(stockPrice11.getPrice());
    private final PricedUserStock pricedUserStock22 = new PricedUserStock()
            .setUserId(userStock22.getUserId())
            .setMarket(userStock22.getMarket())
            .setSymbol(userStock22.getSymbol())
            .setShares(userStock22.getShares())
            .setTimestamp(stockPrice21.getTimestamp())
            .setPrice(stockPrice21.getPrice());

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userStockService = new UserStockServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        pricedUserStockService = new PricedUserStockServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
    }

    @After
    public void cleanup() {
        stockPriceService.truncate();
        stockService.truncate();
        userStockService.truncate();
        userService.truncate();
    }

    @Test
    public void testGetMissing() {
        assertFalse(pricedUserStockService.get("missing-id", TWITTER, "missing-id").isPresent());
    }

    @Test
    public void testGetExistsNoPrice() {
        assertEquals(1, userStockService.add(userStock11));

        PricedUserStock fetched =
                pricedUserStockService.get(userStock11.getUserId(), userStock11.getMarket(), userStock11.getSymbol())
                .orElseThrow(() -> new AssertionFailedError("Not found"));

        assertEquals(userStock11, fetched.asUserStock());
        assertNotNull(fetched.getTimestamp()); // defaults to now
        assertEquals(1, fetched.getPrice()); // defaults to 1
    }

    @Test
    public void testGetExistsSinglePrice() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, stockPriceService.add(stockPrice11));

        PricedUserStock fetched =
                pricedUserStockService.get(userStock11.getUserId(), userStock11.getMarket(), userStock11.getSymbol())
                        .orElseThrow(() -> new AssertionFailedError("Not found"));

        assertEquals(pricedUserStock11, fetched);
    }

    @Test
    public void testGetExistsMultiplePrices() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));

        PricedUserStock fetched =
                pricedUserStockService.get(userStock11.getUserId(), userStock11.getMarket(), userStock11.getSymbol())
                        .orElseThrow(() -> new AssertionFailedError("Not found"));

        assertEquals(pricedUserStock11, fetched);
    }

    @Test
    public void testGetForUserNone() {
        Results<PricedUserStock> results = pricedUserStockService.getForUser(user1.getId(), new Page(), emptyList());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserSomeNoSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));

        Results<PricedUserStock> results = pricedUserStockService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(results, pricedUserStock11, pricedUserStock12);
    }

    @Test
    public void testGetForUserSomeWithSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), PRICE.toSort());
        Results<PricedUserStock> results = pricedUserStockService.getForUser(user1.getId(), new Page(), sort);
        validateResults(results, pricedUserStock12, pricedUserStock11);
    }

    @Test
    public void testGetForStockNone() {
        Results<PricedUserStock> results =
                pricedUserStockService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForStockSomeNoSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock21));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));

        Results<PricedUserStock> results =
                pricedUserStockService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), emptyList());
        validateResults(results, pricedUserStock11, pricedUserStock21);
    }

    @Test
    public void testGetForStockSomeWithSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock21));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));

        List<Sort> sort = asList(USER_ID.toSort(DESC), SYMBOL.toSort());
        Results<PricedUserStock> results =
                pricedUserStockService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), sort);
        validateResults(results, pricedUserStock21, pricedUserStock11);
    }

    @Test
    public void testGetAllNone() {
        Results<PricedUserStock> results = pricedUserStockService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSomeNoSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock21));
        assertEquals(1, userStockService.add(userStock22));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));

        Results<PricedUserStock> results = pricedUserStockService.getAll(new Page(), emptyList());
        validateResults(results, pricedUserStock11, pricedUserStock12, pricedUserStock21, pricedUserStock22);
    }

    @Test
    public void testGetAllSomeWithSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock21));
        assertEquals(1, userStockService.add(userStock22));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));

        List<Sort> sort = asList(USER_ID.toSort(DESC), SYMBOL.toSort());
        Results<PricedUserStock> results = pricedUserStockService.getAll(new Page(), sort);
        validateResults(results, pricedUserStock21, pricedUserStock22, pricedUserStock11, pricedUserStock12);
    }
}
