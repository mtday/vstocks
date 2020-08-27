package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.portfolio.MarketRank;
import vstocks.model.portfolio.MarketRankCollection;
import vstocks.model.portfolio.RankedUser;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.RANK;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class MarketRankServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private StockService stockService;
    private StockPriceService stockPriceService;
    private UserStockService userStockService;
    private MarketRankService marketRankService;

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

    private final MarketRank marketRank11 = new MarketRank()
            .setBatch(2)
            .setUserId(user1.getId())
            .setMarket(TWITTER)
            .setTimestamp(now)
            .setRank(1)
            .setValue(10);
    private final MarketRank marketRank12 = new MarketRank()
            .setBatch(1)
            .setUserId(user1.getId())
            .setMarket(TWITTER)
            .setTimestamp(now.minusSeconds(10))
            .setRank(2)
            .setValue(9);
    private final MarketRank marketRank21 = new MarketRank()
            .setBatch(2)
            .setUserId(user2.getId())
            .setMarket(TWITTER)
            .setTimestamp(now)
            .setRank(2)
            .setValue(10);
    private final MarketRank marketRank22 = new MarketRank()
            .setBatch(1)
            .setUserId(user2.getId())
            .setMarket(TWITTER)
            .setTimestamp(now.minusSeconds(10))
            .setRank(3)
            .setValue(9);

    private final RankedUser rankedUser1 = new RankedUser()
            .setUser(user1)
            .setBatch(marketRank11.getBatch())
            .setTimestamp(marketRank11.getTimestamp())
            .setRank(marketRank11.getRank())
            .setValue(marketRank11.getValue());
    private final RankedUser rankedUser2 = new RankedUser()
            .setUser(user2)
            .setBatch(marketRank21.getBatch())
            .setTimestamp(marketRank21.getTimestamp())
            .setRank(marketRank21.getRank())
            .setValue(marketRank21.getValue());

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        userStockService = new UserStockServiceImpl(dataSourceExternalResource.get());
        marketRankService = new MarketRankServiceImpl(dataSourceExternalResource.get());

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
        marketRankService.truncate();
        userStockService.truncate();
        stockPriceService.truncate();
        stockService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerateTie() {
        userStockService.add(userStock11);
        userStockService.add(userStock21);

        assertEquals(2 * Market.values().length, marketRankService.generate());

        Results<MarketRank> results = marketRankService.getAll(TWITTER, new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals("1,1", results.getResults().stream().map(r -> "" + r.getRank()).collect(joining(",")));
        assertEquals("110,110", results.getResults().stream().map(r -> "" + r.getValue()).collect(joining(",")));
    }

    @Test
    public void testGenerateNoTie() {
        userStockService.add(userStock12);
        userStockService.add(userStock22);

        assertEquals(2 * Market.values().length, marketRankService.generate());

        Results<MarketRank> results = marketRankService.getAll(TWITTER, new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals("1,2", results.getResults().stream().map(r -> "" + r.getRank()).collect(joining(",")));
        assertEquals("420,210", results.getResults().stream().map(r -> "" + r.getValue()).collect(joining(",")));
    }

    @Test
    public void testGetLatestWithMarketNone() {
        MarketRankCollection latest = marketRankService.getLatest(user1.getId(), TWITTER);
        assertTrue(latest.getRanks().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestWithMarketSome() {
        assertEquals(1, marketRankService.add(marketRank11));
        assertEquals(1, marketRankService.add(marketRank12));

        MarketRankCollection latest = marketRankService.getLatest(user1.getId(), TWITTER);
        validateResults(latest.getRanks(), marketRank11, marketRank12);
        assertEquals(getDeltas(-1, -50f), latest.getDeltas());
    }

    @Test
    public void testGetLatestNone() {
        List<MarketRankCollection> latest = marketRankService.getLatest(user1.getId());
        assertEquals(Market.values().length, latest.size());
        latest.forEach(collection -> {
            assertTrue(collection.getRanks().isEmpty());
            assertEquals(getZeroDeltas(), collection.getDeltas());
        });
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, marketRankService.add(marketRank11));
        assertEquals(1, marketRankService.add(marketRank12));

        List<MarketRankCollection> latest = marketRankService.getLatest(user1.getId());
        assertEquals(Market.values().length, latest.size());
        latest.stream()
                .filter(collection -> collection.getMarket() != TWITTER)
                .forEach(collection -> {
                    assertNotNull(collection.getMarket());
                    assertTrue(collection.getRanks().isEmpty());
                    assertEquals(getZeroDeltas(), collection.getDeltas());
                });

        MarketRankCollection twitterRanks = latest.stream()
                .filter(collection -> collection.getMarket() == TWITTER)
                .findFirst()
                .orElse(null);
        assertNotNull(twitterRanks);
        validateResults(twitterRanks.getRanks(), marketRank11, marketRank12);
        assertEquals(getDeltas(-1, -50f), twitterRanks.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<MarketRank> results = marketRankService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, marketRankService.add(marketRank11));
        assertEquals(1, marketRankService.add(marketRank12));

        Results<MarketRank> results = marketRankService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results, marketRank11, marketRank12);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, marketRankService.add(marketRank11));
        assertEquals(1, marketRankService.add(marketRank12));
        assertEquals(1, marketRankService.add(marketRank21));
        assertEquals(1, marketRankService.add(marketRank22));

        List<Sort> sort = asList(RANK.toSort(DESC), USER_ID.toSort());
        Results<MarketRank> results = marketRankService.getAll(TWITTER, new Page(), sort);
        validateResults(results, marketRank22, marketRank12, marketRank21, marketRank11);
    }

    @Test
    public void testGetUsersNone() {
        Results<RankedUser> results = marketRankService.getUsers(TWITTER, new Page());
        validateResults(results);
    }

    @Test
    public void testGetUsersSome() {
        assertEquals(1, marketRankService.add(marketRank11));
        assertEquals(1, marketRankService.add(marketRank12));
        assertEquals(1, marketRankService.add(marketRank21));
        assertEquals(1, marketRankService.add(marketRank22));

        marketRankService.setCurrentBatch(2);
        Results<RankedUser> results = marketRankService.getUsers(TWITTER, new Page());
        validateResults(results, rankedUser1, rankedUser2);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, marketRankService.add(marketRank11));
        assertEquals(0, marketRankService.add(marketRank11));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, marketRankService.add(marketRank11));
        assertEquals(1, marketRankService.add(marketRank12));
        assertEquals(1, marketRankService.add(marketRank21));
        assertEquals(1, marketRankService.add(marketRank22));

        assertEquals(2, marketRankService.ageOff(now.minusSeconds(5)));

        Results<MarketRank> results = marketRankService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results, marketRank11, marketRank21);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, marketRankService.add(marketRank11));
        assertEquals(1, marketRankService.add(marketRank12));
        assertEquals(1, marketRankService.add(marketRank21));
        assertEquals(1, marketRankService.add(marketRank22));

        assertEquals(4, marketRankService.truncate());

        Results<MarketRank> results = marketRankService.getAll(TWITTER, new Page(), emptyList());
        validateResults(results);
    }
}
