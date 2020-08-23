package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.portfolio.MarketValue;
import vstocks.model.portfolio.MarketValueCollection;
import vstocks.model.portfolio.ValuedUser;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.DatabaseField.VALUE;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.User.generateId;

public class MarketValueServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private StockService stockService;
    private StockPriceService stockPriceService;
    private UserStockService userStockService;
    private MarketValueService marketValueService;

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

    private final StockPrice stockPrice11 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now)
            .setPrice(11);
    private final StockPrice stockPrice12 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(12);
    private final StockPrice stockPrice21 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now)
            .setPrice(21);
    private final StockPrice stockPrice22 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(22);

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
            .setShares(20);

    private final MarketValue marketValue11 = new MarketValue()
            .setBatch(2)
            .setUserId(user1.getId())
            .setMarket(TWITTER)
            .setTimestamp(now)
            .setValue(11);
    private final MarketValue marketValue12 = new MarketValue()
            .setBatch(1)
            .setUserId(user1.getId())
            .setMarket(TWITTER)
            .setTimestamp(now.minusSeconds(10))
            .setValue(12);
    private final MarketValue marketValue21 = new MarketValue()
            .setBatch(2)
            .setUserId(user2.getId())
            .setMarket(TWITTER)
            .setTimestamp(now)
            .setValue(21);
    private final MarketValue marketValue22 = new MarketValue()
            .setBatch(1)
            .setUserId(user2.getId())
            .setMarket(TWITTER)
            .setTimestamp(now.minusSeconds(10))
            .setValue(22);

    private final ValuedUser valuedUser1 = new ValuedUser()
            .setUser(user1)
            .setBatch(marketValue11.getBatch())
            .setTimestamp(marketValue11.getTimestamp())
            .setValue(marketValue11.getValue());
    private final ValuedUser valuedUser2 = new ValuedUser()
            .setUser(user2)
            .setBatch(marketValue21.getBatch())
            .setTimestamp(marketValue21.getTimestamp())
            .setValue(marketValue21.getValue());

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        userStockService = new UserStockServiceImpl(dataSourceExternalResource.get());
        marketValueService = new MarketValueServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));
    }

    @After
    public void cleanup() {
        marketValueService.truncate();
        userStockService.truncate();
        stockPriceService.truncate();
        stockService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerateTie() {
        userStockService.add(userStock11);
        userStockService.add(userStock21);

        assertEquals(2 * Market.values().length, marketValueService.generate());

        Results<MarketValue> results = marketValueService.getAll(TWITTER, new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(220, results.getResults().stream().mapToLong(MarketValue::getValue).sum());
    }

    @Test
    public void testGenerateNoTie() {
        userStockService.add(userStock12);
        userStockService.add(userStock22);

        assertEquals(2 * Market.values().length, marketValueService.generate());

        Results<MarketValue> results = marketValueService.getAll(TWITTER, new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(630, results.getResults().stream().mapToLong(MarketValue::getValue).sum());
    }

    @Test
    public void testGetLatestWithMarketNone() {
        MarketValueCollection latest = marketValueService.getLatest(user1.getId(), TWITTER);
        assertTrue(latest.getValues().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestWithMarketSome() {
        assertEquals(1, marketValueService.add(marketValue11));
        assertEquals(1, marketValueService.add(marketValue12));

        MarketValueCollection latest = marketValueService.getLatest(user1.getId(), TWITTER);
        validateResults(latest.getValues(), marketValue11, marketValue12);
        assertEquals(getDeltas(-1, -8.333334f), latest.getDeltas());
    }

    @Test
    public void testGetLatestNone() {
        Map<Market, MarketValueCollection> latestMap = marketValueService.getLatest(user1.getId());
        assertEquals(Market.values().length, latestMap.size());
        latestMap.values().forEach(latest -> {
            assertTrue(latest.getValues().isEmpty());
            assertEquals(getZeroDeltas(), latest.getDeltas());
        });
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, marketValueService.add(marketValue11));
        assertEquals(1, marketValueService.add(marketValue12));

        Map<Market, MarketValueCollection> latestMap = marketValueService.getLatest(user1.getId());
        assertEquals(Market.values().length, latestMap.size());
        latestMap.entrySet().stream()
                .filter(entry -> entry.getKey() != TWITTER)
                .map(Map.Entry::getValue)
                .forEach(latest -> {
                    assertTrue(latest.getValues().isEmpty());
                    assertEquals(getZeroDeltas(), latest.getDeltas());
                });

        MarketValueCollection twitterValues = latestMap.get(TWITTER);
        validateResults(twitterValues.getValues(), marketValue11, marketValue12);
        assertEquals(getDeltas(-1, -8.333334f), twitterValues.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<MarketValue> results = marketValueService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, marketValueService.add(marketValue11));
        assertEquals(1, marketValueService.add(marketValue12));

        Results<MarketValue> results = marketValueService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results, marketValue11, marketValue12);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, marketValueService.add(marketValue11));
        assertEquals(1, marketValueService.add(marketValue12));
        assertEquals(1, marketValueService.add(marketValue21));
        assertEquals(1, marketValueService.add(marketValue22));

        List<Sort> sort = asList(VALUE.toSort(), USER_ID.toSort());
        Results<MarketValue> results = marketValueService.getAll(TWITTER, new Page(), sort);
        validateResults(results, marketValue11, marketValue12, marketValue21, marketValue22);
    }

    @Test
    public void testGetUsersNone() {
        Results<ValuedUser> results = marketValueService.getUsers(TWITTER, new Page());
        validateResults(results);
    }

    @Test
    public void testGetUsersSome() {
        assertEquals(1, marketValueService.add(marketValue11));
        assertEquals(1, marketValueService.add(marketValue12));
        assertEquals(1, marketValueService.add(marketValue21));
        assertEquals(1, marketValueService.add(marketValue22));

        marketValueService.setCurrentBatch(2);
        Results<ValuedUser> results = marketValueService.getUsers(TWITTER, new Page());
        validateResults(results, valuedUser2, valuedUser1);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, marketValueService.add(marketValue11));
        assertEquals(0, marketValueService.add(marketValue11));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, marketValueService.add(marketValue11));
        assertEquals(1, marketValueService.add(marketValue12));
        assertEquals(1, marketValueService.add(marketValue21));
        assertEquals(1, marketValueService.add(marketValue22));

        marketValueService.ageOff(now.minusSeconds(5));

        Results<MarketValue> results = marketValueService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results, marketValue21, marketValue11);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, marketValueService.add(marketValue11));
        assertEquals(1, marketValueService.add(marketValue12));
        assertEquals(1, marketValueService.add(marketValue21));
        assertEquals(1, marketValueService.add(marketValue22));

        marketValueService.truncate();

        Results<MarketValue> results = marketValueService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results);
    }
}
