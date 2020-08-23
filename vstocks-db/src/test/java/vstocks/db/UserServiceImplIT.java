package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Locale.ENGLISH;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.DISPLAY_NAME;
import static vstocks.model.DatabaseField.USERNAME;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class UserServiceImplIT extends BaseServiceImplIT {
    private ActivityLogService activityLogService;
    private StockPriceService stockPriceService;
    private StockService stockService;
    private UserCreditsService userCreditsService;
    private UserService userService;
    private UserStockService userStockService;

    private final User user1 = new User()
            .setId(generateId("user1@domain.com"))
            .setEmail("user1@domain.com")
            .setUsername("username1")
            .setDisplayName("Name1")
            .setProfileImage("link1");
    private final User user2 = new User()
            .setId(generateId("user2@domain.com"))
            .setEmail("user2@domain.com")
            .setUsername("username2")
            .setDisplayName("Name2")
            .setProfileImage("link2");
    private final User user3 = new User()
            .setId(generateId("user3@domain.com"))
            .setEmail("user3@domain.com")
            .setUsername("username3")
            .setDisplayName("Name3")
            .setProfileImage("link3");
    private final User user4 = new User()
            .setId(generateId("user4@domain.com"))
            .setEmail("user4@domain.com")
            .setUsername("username4")
            .setDisplayName("Name4")
            .setProfileImage("link4");
    private final User user5 = new User()
            .setId(generateId("user5@domain.com"))
            .setEmail("user5@domain.com")
            .setUsername("username5")
            .setDisplayName("Name5")
            .setProfileImage("link5");

    private final Stock stock1 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("symbol1")
            .setName("Name1")
            .setProfileImage("link1");
    private final Stock stock2 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("symbol2")
            .setName("Name2")
            .setProfileImage("link2");

    private final StockPrice stockPrice1 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setPrice(2)
            .setTimestamp(now);
    private final StockPrice stockPrice2 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setPrice(3)
            .setTimestamp(now);

    private final UserStock userStock1 = new UserStock()
            .setUserId(user1.getId())
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setShares(3);
    private final UserStock userStock2 = new UserStock()
            .setUserId(user1.getId())
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setShares(4);

    @Before
    public void setup() {
        activityLogService = new ActivityLogServiceImpl(dataSourceExternalResource.get());
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        userCreditsService = new UserCreditsServiceImpl(dataSourceExternalResource.get());
        userService = new UserServiceImpl(dataSourceExternalResource.get());
        userStockService = new UserStockServiceImpl(dataSourceExternalResource.get());
    }

    @After
    public void cleanup() {
        activityLogService.truncate();
        userStockService.truncate();
        stockPriceService.truncate();
        stockService.truncate();
        userCreditsService.truncate();
        userService.truncate();
    }

    @Test
    public void testUsernameExistsMissing() {
        assertFalse(userService.usernameExists("missing"));
    }

    @Test
    public void testUsernameExists() {
        assertEquals(1, userService.add(user1));
        assertTrue(userService.usernameExists(user1.getUsername()));
    }

    @Test
    public void testGetMissing() {
        assertFalse(userService.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        assertEquals(1, userService.add(user1));

        User fetched = userService.get(user1.getId()).orElse(null);
        assertEquals(user1, fetched);
    }

    @Test
    public void testGetNoProfileImage() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("name")
                .setDisplayName("Name");
        assertEquals(1, userService.add(user));

        User fetched = userService.get(user.getId()).orElse(null);
        assertNotNull(fetched);
        assertEquals(user.getId(), fetched.getId());
        assertEquals(user.getEmail().toLowerCase(ENGLISH), fetched.getEmail());
        assertEquals(user.getUsername(), fetched.getUsername());
        assertEquals(user.getDisplayName(), fetched.getDisplayName());
        assertNull(fetched.getProfileImage());
    }

    @Test
    public void testGetAllNone() {
        Results<User> results =  userService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSomeNoSort() {
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));

        Results<User> results = userService.getAll(new Page(), emptyList());
        validateResults(results, user1, user2);
    }

    @Test
    public void testGetAllSomeWithSort() {
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));

        List<Sort> sort = asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort());
        Results<User> results = userService.getAll(new Page(), sort);
        validateResults(results, user2, user1);
    }

    @Test
    public void testGetAllMultiplePagesNoSort() {
        asList(user1, user2, user3, user4, user5).forEach(user -> assertEquals(1, userService.add(user)));

        Page page = new Page().setSize(2);
        Results<User> results = userService.getAll(page, emptyList());
        validateResults(results, 5, 1, user1, user2);

        page = page.next();
        results = userService.getAll(page, emptyList());
        validateResults(results, 5, 2, user3, user4);

        page = page.next();
        results = userService.getAll(page, emptyList());
        validateResults(results, 5, 3, user5);
    }

    @Test
    public void testGetAllMultiplePagesWithSort() {
        asList(user1, user2, user3, user4, user5).forEach(user -> assertEquals(1, userService.add(user)));

        Page page = new Page().setSize(2);
        List<Sort> sort = asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort());
        Results<User> results = userService.getAll(page, sort);
        validateResults(results, 5, 1, user5, user4);

        page = page.next();
        results = userService.getAll(page, sort);
        validateResults(results, 5, 2, user3, user2);

        page = page.next();
        results = userService.getAll(page, sort);
        validateResults(results, 5, 3, user1);
    }

    @Test
    public void testConsumeNone() {
        List<User> results = new ArrayList<>();
        assertEquals(0, userService.consume(results::add, emptyList()));
        validateResults(results);
    }

    @Test
    public void testConsumeSomeNoSort() {
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));

        List<User> results = new ArrayList<>();
        assertEquals(2, userService.consume(results::add, emptyList()));
        validateResults(results, user1, user2);
    }

    @Test
    public void testConsumeSomeWithSort() {
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.add(user2));

        List<User> results = new ArrayList<>();
        List<Sort> sort = asList(USERNAME.toSort(DESC), DISPLAY_NAME.toSort());
        assertEquals(2, userService.consume(results::add, sort));
        validateResults(results, user2, user1);
    }

    @Test
    public void testReset() {
        assertEquals(1, userService.add(user1));

        assertEquals(1, userCreditsService.update(user1.getId(), 1234));
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));
        assertEquals(1, userStockService.add(userStock1));
        assertEquals(1, userStockService.add(userStock2));

        UserCredits userCreditsBeforeReset = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCreditsBeforeReset);
        assertEquals(11234, userCreditsBeforeReset.getCredits());

        Results<UserStock> userStocksBeforeReset = userStockService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(userStocksBeforeReset, userStock1, userStock2);

        userService.reset(user1.getId());

        UserCredits userCreditsAfterReset = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCreditsAfterReset);
        assertEquals(10000, userCreditsAfterReset.getCredits());

        Results<UserStock> userStocksAfterReset = userStockService.getForUser(user1.getId(), new Page(), emptyList());
        validateResults(userStocksAfterReset);
    }

    @Test
    public void testAdd() {
        assertEquals(1, userService.add(user1));

        User fetched = userService.get(user1.getId()).orElse(null);
        assertEquals(user1, fetched);

        UserCredits userCredits = userCreditsService.get(user1.getId()).orElse(null);
        assertNotNull(userCredits);
        assertEquals(10000, userCredits.getCredits());
    }

    @Test(expected = Exception.class)
    public void testAddUsernameConflict() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("name")
                .setDisplayName("Name")
                .setProfileImage("link");
        assertEquals(1, userService.add(user));
        user.setId(generateId("different"));
        user.setEmail("different");
        userService.add(user); // fails on the unique username constraint
    }

    @Test
    public void testAddPrimaryKeyConflictNoChange() {
        assertEquals(1, userService.add(user1));
        assertEquals(0, userService.add(user1));

        User fetched = userService.get(user1.getId()).orElse(null);
        assertEquals(user1, fetched);
    }

    @Test
    public void testAddPrimaryKeyConflictUpdate() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("name")
                .setDisplayName("Name")
                .setProfileImage("link");
        assertEquals(1, userService.add(user));

        user.setUsername("different");
        user.setDisplayName("different");
        user.setProfileImage("different");
        assertEquals(1, userService.add(user));

        User fetched = userService.get(user.getId()).orElse(null);
        assertEquals(user, fetched);
    }

    @Test
    public void testAddPrimaryKeyConflictUpdateLinkToNull() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("name")
                .setDisplayName("Name")
                .setProfileImage("link");
        assertEquals(1, userService.add(user));
        user.setProfileImage(null);
        assertEquals(1, userService.add(user));

        User fetched = userService.get(user.getId()).orElse(null);
        assertEquals(user, fetched);
    }

    @Test
    public void testAddPrimaryKeyConflictUpdateNullLink() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("name")
                .setDisplayName("Name");
        assertEquals(1, userService.add(user));
        user.setProfileImage("link");
        assertEquals(1, userService.add(user));

        User fetched = userService.get(user.getId()).orElse(null);
        assertEquals(user, fetched);
    }

    @Test
    public void testAddPrimaryKeyConflictDifferentCase() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("name")
                .setDisplayName("Name")
                .setProfileImage("link");
        assertEquals(1, userService.add(user));
        user.setEmail("USER@DOMAIN.COM");
        assertEquals(0, userService.add(user));

        user.setEmail("user@domain.com");
        User fetched = userService.get(user.getId()).orElse(null);
        assertEquals(user, fetched);
    }

    @Test
    public void testAddPrimaryKeyConflictDifferentCaseUpdate() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("name")
                .setDisplayName("Name")
                .setProfileImage("link");
        assertEquals(1, userService.add(user));
        user.setEmail("USER@DOMAIN.COM");
        user.setDisplayName("different");
        assertEquals(1, userService.add(user));

        user.setEmail("user@domain.com");
        User fetched = userService.get(user.getId()).orElse(null);
        assertEquals(user, fetched);
    }

    @Test
    public void testUpdateMissing() {
        assertEquals(0, userService.update(user1));
    }

    @Test
    public void testUpdate() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("name")
                .setDisplayName("Name")
                .setProfileImage("link");
        assertEquals(1, userService.add(user));

        user.setUsername("updated");
        user.setDisplayName("updated");
        user.setProfileImage("updated");
        assertEquals(1, userService.update(user));

        User updated = userService.get(user.getId()).orElse(null);
        assertEquals(user, updated);
    }

    @Test
    public void testUpdateLinkToNull() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("name")
                .setDisplayName("Name")
                .setProfileImage("link");
        assertEquals(1, userService.add(user));

        user.setProfileImage(null);
        assertEquals(1, userService.update(user));

        User updated = userService.get(user.getId()).orElse(null);
        assertEquals(user, updated);
    }

    @Test
    public void testUpdateNullLink() {
        User user = new User()
                .setId(generateId("user@domain.com"))
                .setEmail("user@domain.com")
                .setUsername("name")
                .setDisplayName("Name");
        assertEquals(1, userService.add(user));

        user.setProfileImage("link");
        assertEquals(1, userService.update(user));

        User updated = userService.get(user.getId()).orElse(null);
        assertEquals(user, updated);
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, userService.delete("missing"));
    }

    @Test
    public void testDelete() {
        assertEquals(1, userService.add(user1));
        assertEquals(1, userService.delete(user1.getId()));
        assertFalse(userService.get(user1.getId()).isPresent());
    }

    @Test
    public void testTruncate() {
        asList(user1, user2, user3, user4, user5).forEach(user -> assertEquals(1, userService.add(user)));

        assertEquals(5, userService.truncate());
        Results<User> results = userService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
