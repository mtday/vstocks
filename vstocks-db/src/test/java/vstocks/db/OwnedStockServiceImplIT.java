package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.Sort;
import vstocks.model.Stock;
import vstocks.model.User;
import vstocks.model.UserStock;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class OwnedStockServiceImplIT extends BaseServiceImplIT {
    private StockService stockService;
    private UserService userService;
    private UserStockService userStockService;
    private OwnedStockService ownedStockService;

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
    private final Stock stock3 = new Stock()
            .setMarket(YOUTUBE)
            .setSymbol("sym1")
            .setName("name1")
            .setProfileImage("link");
    private final Stock stock4 = new Stock()
            .setMarket(YOUTUBE)
            .setSymbol("sym2")
            .setName("name2")
            .setProfileImage("link");

    private final User user1 = new User()
            .setId(generateId("user1@domain.com"))
            .setEmail("user1@domain.com")
            .setUsername("user1")
            .setDisplayName("User 1");
    private final User user2 = new User()
            .setId(generateId("user2@domain.com"))
            .setEmail("user2@domain.com")
            .setUsername("user2")
            .setDisplayName("User 2");
    private final User user3 = new User()
            .setId(generateId("user3@domain.com"))
            .setEmail("user3@domain.com")
            .setUsername("user3")
            .setDisplayName("User 3");

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
    private final UserStock userStock22 = new UserStock()
            .setUserId(user2.getId())
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(22);
    private final UserStock userStock33 = new UserStock()
            .setUserId(user3.getId())
            .setMarket(stock3.getMarket())
            .setSymbol(stock3.getSymbol())
            .setShares(33);

    @Before
    public void setup() {
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userStockService = new UserStockServiceImpl(dataSourceExternalResource.get());
        ownedStockService = new OwnedStockServiceImpl(dataSourceExternalResource.get());

        // Clean out the stocks added via flyway
        stockService.truncate();

        // Add some stocks and users for testing
        stockService.add(stock1);
        stockService.add(stock2);
        stockService.add(stock3);
        stockService.add(stock4);

        userService.add(user1);
        userService.add(user2);
        userService.add(user3);
    }

    @After
    public void cleanup() {
        userStockService.truncate();
        stockService.truncate();
        userService.truncate();
    }

    @Test
    public void testConsumeForMarketNone() {
        List<Stock> results = new ArrayList<>();
        assertEquals(0, ownedStockService.consumeForMarket(TWITTER, results::add, emptyList()));
        validateResults(results);
    }

    @Test
    public void testConsumeForMarketSomeNoSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock22));
        assertEquals(1, userStockService.add(userStock33));

        List<Stock> results = new ArrayList<>();
        assertEquals(2, ownedStockService.consumeForMarket(TWITTER, results::add, emptyList()));
        validateResults(results, stock1, stock2);
    }

    @Test
    public void testConsumeForMarketSomeWithSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock22));
        assertEquals(1, userStockService.add(userStock33));

        List<Stock> results = new ArrayList<>();
        List<Sort> sort = asList(SYMBOL.toSort(DESC), NAME.toSort());
        assertEquals(2, ownedStockService.consumeForMarket(TWITTER, results::add, sort));
        validateResults(results, stock2, stock1);
    }

    @Test
    public void testConsumeNone() {
        List<Stock> results = new ArrayList<>();
        assertEquals(0, ownedStockService.consume(results::add, emptyList()));
        validateResults(results);
    }

    @Test
    public void testConsumeSomeNoSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock22));
        assertEquals(1, userStockService.add(userStock33));

        List<Stock> results = new ArrayList<>();
        assertEquals(3, ownedStockService.consume(results::add, emptyList()));
        validateResults(results, stock1, stock2, stock3);
    }

    @Test
    public void testConsumeSomeWithSort() {
        assertEquals(1, userStockService.add(userStock11));
        assertEquals(1, userStockService.add(userStock12));
        assertEquals(1, userStockService.add(userStock22));
        assertEquals(1, userStockService.add(userStock33));

        List<Stock> results = new ArrayList<>();
        List<Sort> sort = asList(SYMBOL.toSort(DESC), MARKET.toSort());
        assertEquals(3, ownedStockService.consume(results::add, sort));
        validateResults(results, stock2, stock1, stock3);
    }
}
