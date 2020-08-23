package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.portfolio.MarketTotalRank;
import vstocks.model.portfolio.MarketTotalRankCollection;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.RANK;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class MarketTotalRankServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private StockService stockService;
    private StockPriceService stockPriceService;
    private UserStockService userStockService;
    private MarketTotalRankService marketTotalRankService;

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

    private final MarketTotalRank marketTotalRank11 = new MarketTotalRank()
            .setUserId(user1.getId())
            .setTimestamp(now)
            .setRank(1);
    private final MarketTotalRank marketTotalRank12 = new MarketTotalRank()
            .setUserId(user1.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(2);
    private final MarketTotalRank marketTotalRank21 = new MarketTotalRank()
            .setUserId(user2.getId())
            .setTimestamp(now)
            .setRank(2);
    private final MarketTotalRank marketTotalRank22 = new MarketTotalRank()
            .setUserId(user2.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(3);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        userStockService = new UserStockServiceImpl(dataSourceExternalResource.get());
        marketTotalRankService = new MarketTotalRankServiceImpl(dataSourceExternalResource.get());

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
        marketTotalRankService.truncate();
        userStockService.truncate();
        stockPriceService.truncate();
        stockService.truncate();
        userService.truncate();
    }

    @Test
    public void testGenerateTie() {
        userStockService.add(userStock11);
        userStockService.add(userStock21);

        assertEquals(2, marketTotalRankService.generate());

        Results<MarketTotalRank> results = marketTotalRankService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().stream().map(MarketTotalRank::getRank).allMatch(rank -> rank == 1));
    }

    @Test
    public void testGenerateNoTie() {
        userStockService.add(userStock12);
        userStockService.add(userStock22);

        assertEquals(2, marketTotalRankService.generate());

        Results<MarketTotalRank> results = marketTotalRankService.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(3, results.getResults().stream().mapToLong(MarketTotalRank::getRank).sum());
    }

    @Test
    public void testGetLatestNone() {
        MarketTotalRankCollection latest = marketTotalRankService.getLatest(user1.getId());
        assertTrue(latest.getRanks().isEmpty());
        assertEquals(getZeroDeltas(), latest.getDeltas());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, marketTotalRankService.add(marketTotalRank11));
        assertEquals(1, marketTotalRankService.add(marketTotalRank12));

        MarketTotalRankCollection latest = marketTotalRankService.getLatest(user1.getId());
        validateResults(latest.getRanks(), marketTotalRank11, marketTotalRank12);
        assertEquals(getDeltas(-1, -50f), latest.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<MarketTotalRank> results = marketTotalRankService.getAll(new Page(), emptySet());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, marketTotalRankService.add(marketTotalRank11));
        assertEquals(1, marketTotalRankService.add(marketTotalRank12));

        Results<MarketTotalRank> results = marketTotalRankService.getAll(new Page(), emptySet());
        validateResults(results, marketTotalRank11, marketTotalRank12);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, marketTotalRankService.add(marketTotalRank11));
        assertEquals(1, marketTotalRankService.add(marketTotalRank12));
        assertEquals(1, marketTotalRankService.add(marketTotalRank21));
        assertEquals(1, marketTotalRankService.add(marketTotalRank22));

        Set<Sort> sort = new LinkedHashSet<>(asList(RANK.toSort(DESC), USER_ID.toSort()));
        Results<MarketTotalRank> results = marketTotalRankService.getAll(new Page(), sort);
        validateResults(results, marketTotalRank22, marketTotalRank12, marketTotalRank21, marketTotalRank11);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, marketTotalRankService.add(marketTotalRank11));
        assertEquals(0, marketTotalRankService.add(marketTotalRank11));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, marketTotalRankService.add(marketTotalRank11));
        assertEquals(1, marketTotalRankService.add(marketTotalRank12));
        assertEquals(1, marketTotalRankService.add(marketTotalRank21));
        assertEquals(1, marketTotalRankService.add(marketTotalRank22));

        marketTotalRankService.ageOff(now.minusSeconds(5));

        Results<MarketTotalRank> results = marketTotalRankService.getAll(new Page(), emptySet());
        validateResults(results, marketTotalRank11, marketTotalRank21);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, marketTotalRankService.add(marketTotalRank11));
        assertEquals(1, marketTotalRankService.add(marketTotalRank12));
        assertEquals(1, marketTotalRankService.add(marketTotalRank21));
        assertEquals(1, marketTotalRankService.add(marketTotalRank22));

        marketTotalRankService.truncate();

        Results<MarketTotalRank> results = marketTotalRankService.getAll(new Page(), emptySet());
        validateResults(results);
    }
}
