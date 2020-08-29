package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.portfolio.*;

import java.util.List;

import static org.junit.Assert.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.User.generateId;

public class PortfolioValueServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private UserCreditsService userCreditsService;
    private UserStockService userStockService;
    private StockService stockService;
    private StockPriceService stockPriceService;
    private CreditRankService creditRankService;
    private MarketRankService marketRankService;
    private MarketTotalRankService marketTotalRankService;
    private TotalRankService totalRankService;
    private PortfolioValueSummaryService portfolioValueSummaryService;
    private PortfolioValueService portfolioValueService;

    private final User user = new User()
            .setId(generateId("user1@domain.com"))
            .setEmail("user1@domain.com")
            .setUsername("name1")
            .setDisplayName("Name1");

    private final UserCredits userCredits = new UserCredits()
            .setUserId(user.getId())
            .setCredits(30);

    private final Stock stock1 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("sym1")
            .setName("name1")
            .setProfileImage("link1");
    private final Stock stock2 = new Stock()
            .setMarket(YOUTUBE)
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

    private final UserStock userStock1 = new UserStock()
            .setUserId(user.getId())
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setShares(10);
    private final UserStock userStock2 = new UserStock()
            .setUserId(user.getId())
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(20);

    private final CreditRank creditRank1 = new CreditRank()
            .setBatch(2)
            .setUserId(user.getId())
            .setTimestamp(now)
            .setRank(1)
            .setValue(10);
    private final CreditRank creditRank2 = new CreditRank()
            .setBatch(1)
            .setUserId(user.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(2)
            .setValue(9);

    private final MarketRank marketRank11 = new MarketRank()
            .setBatch(2)
            .setUserId(user.getId())
            .setMarket(TWITTER)
            .setTimestamp(now)
            .setRank(1)
            .setValue(10);
    private final MarketRank marketRank12 = new MarketRank()
            .setBatch(1)
            .setUserId(user.getId())
            .setMarket(TWITTER)
            .setTimestamp(now.minusSeconds(10))
            .setRank(2)
            .setValue(9);
    private final MarketRank marketRank21 = new MarketRank()
            .setBatch(2)
            .setUserId(user.getId())
            .setMarket(YOUTUBE)
            .setTimestamp(now)
            .setRank(7)
            .setValue(10);
    private final MarketRank marketRank22 = new MarketRank()
            .setBatch(1)
            .setUserId(user.getId())
            .setMarket(YOUTUBE)
            .setTimestamp(now.minusSeconds(10))
            .setRank(9)
            .setValue(9);

    private final MarketTotalRank marketTotalRank1 = new MarketTotalRank()
            .setBatch(2)
            .setUserId(user.getId())
            .setTimestamp(now)
            .setRank(4)
            .setValue(10);
    private final MarketTotalRank marketTotalRank2 = new MarketTotalRank()
            .setBatch(1)
            .setUserId(user.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(3)
            .setValue(9);

    private final TotalRank totalRank1 = new TotalRank()
            .setBatch(2)
            .setUserId(user.getId())
            .setTimestamp(now)
            .setRank(5)
            .setValue(10);
    private final TotalRank totalRank2 = new TotalRank()
            .setBatch(1)
            .setUserId(user.getId())
            .setTimestamp(now.minusSeconds(10))
            .setRank(7)
            .setValue(9);

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userStockService = new UserStockServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        creditRankService = new CreditRankServiceImpl(dataSourceExternalResource.get());
        marketRankService = new MarketRankServiceImpl(dataSourceExternalResource.get());
        marketTotalRankService = new MarketTotalRankServiceImpl(dataSourceExternalResource.get());
        totalRankService = new TotalRankServiceImpl(dataSourceExternalResource.get());
        portfolioValueSummaryService = new PortfolioValueSummaryServiceImpl(dataSourceExternalResource.get());
        portfolioValueService = new PortfolioValueServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user));
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));
        assertEquals(1, userCreditsService.delete(user.getId()));
        assertEquals(1, userCreditsService.add(userCredits));
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));
        assertEquals(1, creditRankService.add(creditRank1));
        assertEquals(1, creditRankService.add(creditRank2));
        assertEquals(1, marketRankService.add(marketRank11));
        assertEquals(1, marketRankService.add(marketRank12));
        assertEquals(1, marketRankService.add(marketRank21));
        assertEquals(1, marketRankService.add(marketRank22));
        assertEquals(1, marketTotalRankService.add(marketTotalRank1));
        assertEquals(1, marketTotalRankService.add(marketTotalRank2));
        assertEquals(1, totalRankService.add(totalRank1));
        assertEquals(1, totalRankService.add(totalRank2));

    }

    @After
    public void cleanup() {
        creditRankService.truncate();
        marketRankService.truncate();
        marketTotalRankService.truncate();
        totalRankService.truncate();
        userCreditsService.truncate();
        userStockService.truncate();
        stockPriceService.truncate();
        stockService.truncate();
        userService.truncate();
    }

    @Test
    public void testGetForUserMissing() {
        assertFalse(portfolioValueService.getForUser("missing-id").isPresent());
    }

    @Test
    public void testGetForUser() {
        PortfolioValue fetched = portfolioValueService.getForUser(user.getId()).orElse(null);
        assertNotNull(fetched);

        PortfolioValueSummary summary = portfolioValueSummaryService.getForUser(user.getId()).orElse(null);
        assertEquals(summary, fetched.getSummary());

        CreditRankCollection creditRanks = creditRankService.getLatest(user.getId());
        assertEquals(creditRanks, fetched.getCreditRanks());

        List<MarketRankCollection> marketRanks = marketRankService.getLatest(user.getId());
        assertEquals(marketRanks, fetched.getMarketRanks());

        MarketTotalRankCollection marketTotalRanks = marketTotalRankService.getLatest(user.getId());
        assertEquals(marketTotalRanks, fetched.getMarketTotalRanks());

        TotalRankCollection totalRanks = totalRankService.getLatest(user.getId());
        assertEquals(totalRanks, fetched.getTotalRanks());
    }
}
