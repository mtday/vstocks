package vstocks.db.system;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.system.OverallMarketValue;
import vstocks.model.system.OverallMarketValueCollection;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.TIMESTAMP;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.User.generateId;

public class OverallMarketValueServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private StockService stockService;
    private StockPriceService stockPriceService;
    private UserStockService userStockService;
    private OverallMarketValueService overallMarketValueService;

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

    private final OverallMarketValue overallMarketValue1 = new OverallMarketValue()
            .setMarket(TWITTER)
            .setTimestamp(now)
            .setValue(10);
    private final OverallMarketValue overallMarketValue2 = new OverallMarketValue()
            .setMarket(TWITTER)
            .setTimestamp(now.minusSeconds(10))
            .setValue(9);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        userStockService = new UserStockServiceImpl(dataSourceExternalResource.get());
        overallMarketValueService = new OverallMarketValueServiceImpl(dataSourceExternalResource.get());

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
        overallMarketValueService.truncate();
        userStockService.truncate();
        stockPriceService.truncate();
        stockService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerate() {
        userStockService.add(userStock11);
        userStockService.add(userStock12);
        userStockService.add(userStock21);
        userStockService.add(userStock22);

        assertEquals(Market.values().length, overallMarketValueService.generate());

        Results<OverallMarketValue> results = overallMarketValueService.getAll(TWITTER, new Page(), emptyList());
        assertEquals(Page.from(1, 20, 1, 1), results.getPage());
        assertEquals(1, results.getResults().size());
        assertEquals(740, results.getResults().iterator().next().getValue());
    }

    @Test
    public void testGetLatestWithMarketNone() {
        OverallMarketValueCollection latest = overallMarketValueService.getLatest(TWITTER);
        assertTrue(latest.getValues().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestWithMarketSome() {
        assertEquals(1, overallMarketValueService.add(overallMarketValue1));
        assertEquals(1, overallMarketValueService.add(overallMarketValue2));

        OverallMarketValueCollection latest = overallMarketValueService.getLatest(TWITTER);
        validateResults(latest.getValues(), overallMarketValue1, overallMarketValue2);
        assertEquals(getDeltas(9L, 10L, 1, 11.111112f), latest.getDeltas());
    }

    @Test
    public void testGetLatestNone() {
        Map<Market, OverallMarketValueCollection> latestMap = overallMarketValueService.getLatest();
        assertEquals(Market.values().length, latestMap.size());
        latestMap.values().forEach(latest -> {
            assertTrue(latest.getValues().isEmpty());
            assertEquals(getZeroDeltas(), latest.getDeltas());
        });
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, overallMarketValueService.add(overallMarketValue1));
        assertEquals(1, overallMarketValueService.add(overallMarketValue2));

        Map<Market, OverallMarketValueCollection> latestMap = overallMarketValueService.getLatest();
        assertEquals(Market.values().length, latestMap.size());
        latestMap.entrySet().stream()
                .filter(entry -> entry.getKey() != TWITTER)
                .map(Map.Entry::getValue)
                .forEach(latest -> {
                    assertTrue(latest.getValues().isEmpty());
                    assertEquals(getZeroDeltas(), latest.getDeltas());
                });

        OverallMarketValueCollection twitterRanks = latestMap.get(TWITTER);
        validateResults(twitterRanks.getValues(), overallMarketValue1, overallMarketValue2);
        assertEquals(getDeltas(9L, 10L, 1, 11.111112f), twitterRanks.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<OverallMarketValue> results = overallMarketValueService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, overallMarketValueService.add(overallMarketValue1));
        assertEquals(1, overallMarketValueService.add(overallMarketValue2));

        Results<OverallMarketValue> results = overallMarketValueService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results, overallMarketValue1, overallMarketValue2);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, overallMarketValueService.add(overallMarketValue1));
        assertEquals(1, overallMarketValueService.add(overallMarketValue2));

        List<Sort> sort = singletonList(TIMESTAMP.toSort());
        Results<OverallMarketValue> results = overallMarketValueService.getAll(TWITTER, new Page(), sort);
        validateResults(results, overallMarketValue2, overallMarketValue1);
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        assertEquals(1, overallMarketValueService.add(overallMarketValue1));
        overallMarketValueService.add(overallMarketValue1);
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, overallMarketValueService.add(overallMarketValue1));
        assertEquals(1, overallMarketValueService.add(overallMarketValue2));

        assertEquals(1, overallMarketValueService.ageOff(now.minusSeconds(5)));

        Results<OverallMarketValue> results = overallMarketValueService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results, overallMarketValue1);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, overallMarketValueService.add(overallMarketValue1));
        assertEquals(1, overallMarketValueService.add(overallMarketValue2));

        assertEquals(2, overallMarketValueService.truncate());

        Results<OverallMarketValue> results = overallMarketValueService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results);
    }
}
