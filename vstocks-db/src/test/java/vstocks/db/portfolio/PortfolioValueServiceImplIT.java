package vstocks.db.portfolio;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;
import vstocks.model.portfolio.PortfolioValue;

import java.util.Map;

import static org.junit.Assert.*;
import static vstocks.model.Market.*;
import static vstocks.model.User.generateId;

public class PortfolioValueServiceImplIT extends BaseServiceImplIT {
    private UserService userService;
    private UserCreditsService userCreditsService;
    private UserStockService userStockService;
    private StockService stockService;
    private StockPriceService stockPriceService;
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

    @Before
    public void setup() {
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userStockService = new UserStockServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        portfolioValueService = new PortfolioValueServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, userService.add(user));
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));
    }

    @After
    public void cleanup() {
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
        assertEquals(1, userCreditsService.delete(user.getId()));
        assertEquals(1, userCreditsService.add(userCredits));
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        PortfolioValue fetched = portfolioValueService.getForUser(user.getId()).orElse(null);
        assertNotNull(fetched);

        PortfolioValue expected = new PortfolioValue()
                .setUserId(user.getId())
                .setCredits(userCredits.getCredits())
                .setMarketTotal(
                        (userStock1.getShares() * stockPrice11.getPrice()) +
                        (userStock2.getShares() * stockPrice21.getPrice())
                ).setMarketValues(
                        Map.of(
                                TWITTER, userStock1.getShares() * stockPrice11.getPrice(),
                                YOUTUBE, userStock2.getShares() * stockPrice21.getPrice(),
                                INSTAGRAM, 0L,
                                TWITCH, 0L,
                                FACEBOOK, 0L
                        )
                ).setTotal(
                        userCredits.getCredits() +
                        (userStock1.getShares() * stockPrice11.getPrice()) +
                        (userStock2.getShares() * stockPrice21.getPrice())
                );
        assertEquals(expected, fetched);
    }
}
