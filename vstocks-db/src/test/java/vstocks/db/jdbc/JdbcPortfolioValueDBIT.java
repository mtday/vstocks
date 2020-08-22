package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.*;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.*;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.CREDITS;
import static vstocks.model.DatabaseField.USER_ID;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class JdbcPortfolioValueDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserDB userTable;
    private StockDB stockTable;
    private StockPriceDB stockPriceTable;
    private UserCreditsDB userCreditsTable;
    private UserStockDB userStockTable;
    private PortfolioValueTable portfolioValueTable;

    private JdbcPortfolioValueDB portfolioValueDB;

    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
    private final Stock twitterStock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setProfileImage("link");
    private final Stock twitterStock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setProfileImage("link");
    private final Stock twitterStock3 = new Stock().setMarket(TWITTER).setSymbol("sym3").setName("name3").setProfileImage("link");
    private final Stock youtubeStock1 = new Stock().setMarket(YOUTUBE).setSymbol("sym1").setName("name1").setProfileImage("link");
    private final Stock youtubeStock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setProfileImage("link");
    private final Stock youtubeStock3 = new Stock().setMarket(YOUTUBE).setSymbol("sym3").setName("name3").setProfileImage("link");
    private final StockPrice twitterStockPrice1 = new StockPrice().setMarket(twitterStock1.getMarket()).setSymbol(twitterStock1.getSymbol()).setTimestamp(now).setPrice(100);
    private final StockPrice twitterStockPrice2 = new StockPrice().setMarket(twitterStock2.getMarket()).setSymbol(twitterStock2.getSymbol()).setTimestamp(now).setPrice(200);
    private final StockPrice twitterStockPrice3 = new StockPrice().setMarket(twitterStock3.getMarket()).setSymbol(twitterStock3.getSymbol()).setTimestamp(now).setPrice(300);
    private final StockPrice youtubeStockPrice1 = new StockPrice().setMarket(youtubeStock1.getMarket()).setSymbol(youtubeStock1.getSymbol()).setTimestamp(now).setPrice(110);
    private final StockPrice youtubeStockPrice2 = new StockPrice().setMarket(youtubeStock2.getMarket()).setSymbol(youtubeStock2.getSymbol()).setTimestamp(now).setPrice(220);
    private final StockPrice youtubeStockPrice3 = new StockPrice().setMarket(youtubeStock3.getMarket()).setSymbol(youtubeStock3.getSymbol()).setTimestamp(now).setPrice(330);

    @Before
    public void setup() throws SQLException {
        userTable = new UserDB();
        stockTable = new StockDB();
        stockPriceTable = new StockPriceDB();
        userCreditsTable = new UserCreditsDB();
        userStockTable = new UserStockDB();
        portfolioValueTable = new PortfolioValueTable();
        portfolioValueDB = new JdbcPortfolioValueDB(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            assertEquals(1, stockTable.add(connection, twitterStock1));
            assertEquals(1, stockTable.add(connection, twitterStock2));
            assertEquals(1, stockTable.add(connection, twitterStock3));
            assertEquals(1, stockTable.add(connection, youtubeStock1));
            assertEquals(1, stockTable.add(connection, youtubeStock2));
            assertEquals(1, stockTable.add(connection, youtubeStock3));
            assertEquals(1, stockPriceTable.add(connection, twitterStockPrice1));
            assertEquals(1, stockPriceTable.add(connection, twitterStockPrice2));
            assertEquals(1, stockPriceTable.add(connection, twitterStockPrice3));
            assertEquals(1, stockPriceTable.add(connection, youtubeStockPrice1));
            assertEquals(1, stockPriceTable.add(connection, youtubeStockPrice2));
            assertEquals(1, stockPriceTable.add(connection, youtubeStockPrice3));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            portfolioValueTable.truncate(connection);
            userStockTable.truncate(connection);
            stockPriceTable.truncate(connection);
            stockTable.truncate(connection);
            userCreditsTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    private Map<DeltaInterval, Delta> getDeltas(int change, float percent) {
        Map<DeltaInterval, Delta> deltas = new TreeMap<>();
        stream(DeltaInterval.values())
                .map(interval -> new Delta().setInterval(interval).setChange(change).setPercent(percent))
                .forEach(delta -> deltas.put(delta.getInterval(), delta));
        return deltas;
    }

    @Test
    public void testGenerateForUserMissing() {
        PortfolioValue portfolioValue = portfolioValueDB.generateForUser("missing-id");
        assertEquals("missing-id", portfolioValue.getUserId());
        assertEquals(0, portfolioValue.getCredits());
        assertEquals(Market.values().length, portfolioValue.getMarketValues().size());
        assertEquals(0, portfolioValue.getMarketValues().values().stream().mapToLong(l -> l).sum());
        assertEquals(0, portfolioValue.getTotal());
    }

    @Test
    public void testGenerateExistsNoCreditsOrStocks() {
        PortfolioValue portfolioValue = portfolioValueDB.generateForUser(user1.getId());
        assertEquals(user1.getId(), portfolioValue.getUserId());
        assertEquals(0, portfolioValue.getCredits());
        assertEquals(Market.values().length, portfolioValue.getMarketValues().size());
        assertEquals(0, portfolioValue.getMarketValues().values().stream().mapToLong(l -> l).sum());
        assertEquals(0, portfolioValue.getTotal());
    }

    @Test
    public void testGenerateExistsWithCreditsNoStocks() throws SQLException {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10000);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userCreditsTable.add(connection, userCredits));
            connection.commit();
        }

        PortfolioValue fetched = portfolioValueDB.generateForUser(user1.getId());
        assertEquals(user1.getId(), fetched.getUserId());
        assertEquals(userCredits.getCredits(), fetched.getCredits());
        assertEquals(Market.values().length, fetched.getMarketValues().size());
        assertEquals(0, fetched.getMarketValues().values().stream().mapToLong(l -> l).sum());
        assertEquals(userCredits.getCredits(), fetched.getTotal());
    }

    @Test
    public void testGenerateExistsWithCreditsAndSingleStock() throws SQLException {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10000);
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(twitterStock1.getMarket()).setSymbol(twitterStock1.getSymbol()).setShares(30);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userCreditsTable.add(connection, userCredits));
            assertEquals(1, userStockTable.add(connection, userStock));
            connection.commit();
        }

        PortfolioValue fetched = portfolioValueDB.generateForUser(user1.getId());
        assertEquals(user1.getId(), fetched.getUserId());
        assertEquals(userCredits.getCredits(), fetched.getCredits());
        Map<Market, Long> marketValues = fetched.getMarketValues();
        assertEquals(Market.values().length, marketValues.size());
        assertTrue(marketValues.containsKey(TWITTER));
        assertEquals(twitterStockPrice1.getPrice() * userStock.getShares(), (long) marketValues.get(TWITTER));
        assertEquals(userCredits.getCredits() + twitterStockPrice1.getPrice() * userStock.getShares(), fetched.getTotal());
    }

    @Test
    public void testGenerateExistsWithCreditsAndMultipleStocks() throws SQLException {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10000);
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(twitterStock1.getMarket()).setSymbol(twitterStock1.getSymbol()).setShares(30);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(youtubeStock1.getMarket()).setSymbol(youtubeStock1.getSymbol()).setShares(20);
        UserStock userStock3 = new UserStock().setUserId(user1.getId()).setMarket(youtubeStock2.getMarket()).setSymbol(youtubeStock2.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userCreditsTable.add(connection, userCredits));
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            assertEquals(1, userStockTable.add(connection, userStock3));
            connection.commit();
        }

        PortfolioValue fetched = portfolioValueDB.generateForUser(user1.getId());
        assertEquals(user1.getId(), fetched.getUserId());
        assertEquals(userCredits.getCredits(), fetched.getCredits());
        Map<Market, Long> marketValues = fetched.getMarketValues();
        assertEquals(Market.values().length, marketValues.size());
        assertTrue(marketValues.containsKey(TWITTER));
        assertTrue(marketValues.containsKey(YOUTUBE));
        assertEquals(twitterStockPrice1.getPrice() * userStock1.getShares(), (long) marketValues.get(TWITTER));
        assertEquals(youtubeStockPrice1.getPrice() * userStock2.getShares()
                + youtubeStockPrice2.getPrice() * userStock3.getShares(), (long) marketValues.get(YOUTUBE));
        assertEquals(userCredits.getCredits()
                + twitterStockPrice1.getPrice() * userStock1.getShares()
                + youtubeStockPrice1.getPrice() * userStock2.getShares()
                + youtubeStockPrice2.getPrice() * userStock3.getShares(), fetched.getTotal());
    }

    @Test
    public void testGenerateAllNone() {
        List<PortfolioValue> results = new ArrayList<>();
        assertEquals(0, portfolioValueDB.generateAll(results::add));
        assertTrue(results.isEmpty());
    }

    @Test
    public void testGenerateAll() throws SQLException {
        UserCredits userCredits1 = new UserCredits().setUserId(user1.getId()).setCredits(10000);
        UserCredits userCredits2 = new UserCredits().setUserId(user2.getId()).setCredits(11000);
        UserStock userStock11 = new UserStock().setUserId(user1.getId()).setMarket(twitterStock1.getMarket()).setSymbol(twitterStock1.getSymbol()).setShares(30);
        UserStock userStock12 = new UserStock().setUserId(user1.getId()).setMarket(youtubeStock1.getMarket()).setSymbol(youtubeStock1.getSymbol()).setShares(20);
        UserStock userStock13 = new UserStock().setUserId(user1.getId()).setMarket(youtubeStock2.getMarket()).setSymbol(youtubeStock2.getSymbol()).setShares(10);
        UserStock userStock21 = new UserStock().setUserId(user2.getId()).setMarket(twitterStock1.getMarket()).setSymbol(twitterStock1.getSymbol()).setShares(30);
        UserStock userStock22 = new UserStock().setUserId(user2.getId()).setMarket(twitterStock2.getMarket()).setSymbol(twitterStock2.getSymbol()).setShares(20);
        UserStock userStock23 = new UserStock().setUserId(user2.getId()).setMarket(twitterStock3.getMarket()).setSymbol(twitterStock3.getSymbol()).setShares(20);
        UserStock userStock24 = new UserStock().setUserId(user2.getId()).setMarket(youtubeStock3.getMarket()).setSymbol(youtubeStock3.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userCreditsTable.add(connection, userCredits1));
            assertEquals(1, userCreditsTable.add(connection, userCredits2));
            assertEquals(1, userStockTable.add(connection, userStock11));
            assertEquals(1, userStockTable.add(connection, userStock12));
            assertEquals(1, userStockTable.add(connection, userStock13));
            assertEquals(1, userStockTable.add(connection, userStock21));
            assertEquals(1, userStockTable.add(connection, userStock22));
            assertEquals(1, userStockTable.add(connection, userStock23));
            assertEquals(1, userStockTable.add(connection, userStock24));
            connection.commit();
        }

        List<PortfolioValue> results = new ArrayList<>();
        assertEquals(2, portfolioValueDB.generateAll(results::add));
        assertEquals(2, results.size());

        // user2 portfolio has higher value so it is returned first
        PortfolioValue portfolioValue1 = results.get(0);
        assertEquals(user2.getId(), portfolioValue1.getUserId());
        assertEquals(userCredits2.getCredits(), portfolioValue1.getCredits());
        Map<Market, Long> marketValues1 = portfolioValue1.getMarketValues();
        assertEquals(Market.values().length, marketValues1.size());
        assertTrue(marketValues1.containsKey(TWITTER));
        assertTrue(marketValues1.containsKey(YOUTUBE));
        assertEquals(twitterStockPrice1.getPrice() * userStock21.getShares()
                + twitterStockPrice2.getPrice() * userStock22.getShares()
                + twitterStockPrice3.getPrice() * userStock23.getShares(), (long) marketValues1.get(TWITTER));
        assertEquals(youtubeStockPrice3.getPrice() * userStock24.getShares(), (long) marketValues1.get(YOUTUBE));
        assertEquals(userCredits2.getCredits()
                + twitterStockPrice1.getPrice() * userStock21.getShares()
                + twitterStockPrice2.getPrice() * userStock22.getShares()
                + twitterStockPrice3.getPrice() * userStock23.getShares()
                + youtubeStockPrice3.getPrice() * userStock24.getShares(), portfolioValue1.getTotal());

        PortfolioValue portfolioValue2 = results.get(1);
        assertEquals(user1.getId(), portfolioValue2.getUserId());
        assertEquals(userCredits1.getCredits(), portfolioValue2.getCredits());
        Map<Market, Long> marketValues2 = portfolioValue2.getMarketValues();
        assertEquals(Market.values().length, marketValues2.size());
        assertTrue(marketValues2.containsKey(TWITTER));
        assertTrue(marketValues2.containsKey(YOUTUBE));
        assertEquals(twitterStockPrice1.getPrice() * userStock11.getShares(), (long) marketValues2.get(TWITTER));
        assertEquals(youtubeStockPrice1.getPrice() * userStock12.getShares()
                + youtubeStockPrice2.getPrice() * userStock13.getShares(), (long) marketValues2.get(YOUTUBE));
        assertEquals(userCredits1.getCredits()
                + twitterStockPrice1.getPrice() * userStock11.getShares()
                + youtubeStockPrice1.getPrice() * userStock12.getShares()
                + youtubeStockPrice2.getPrice() * userStock13.getShares(), portfolioValue2.getTotal());
    }

    @Test
    public void testGetLatestMissing() {
        PortfolioValueCollection collection = portfolioValueDB.getLatest("missing-id");
        assertTrue(collection.getValues().isEmpty());
        assertEquals(getDeltas(0, 0f), collection.getDeltas());
    }

    @Test
    public void testGetLatestSingle() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValue portfolioValue = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(1010);
        assertEquals(1, portfolioValueDB.add(portfolioValue));

        PortfolioValueCollection collection = portfolioValueDB.getLatest(user1.getId());
        assertEquals(1, collection.getValues().size());
        assertEquals(portfolioValue, collection.getValues().iterator().next());
        assertEquals(getDeltas(0, 0f), collection.getDeltas());
    }

    @Test
    public void testGetLatestMultiple() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValue portfolioValue1 = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(1010);
        PortfolioValue portfolioValue2 = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now.minusSeconds(20)).setCredits(10).setMarketValues(marketValues).setTotal(2020);
        assertEquals(1, portfolioValueDB.add(portfolioValue1));
        assertEquals(1, portfolioValueDB.add(portfolioValue2));

        PortfolioValueCollection collection = portfolioValueDB.getLatest(user1.getId());
        assertEquals(2, collection.getValues().size());
        assertEquals(portfolioValue1, collection.getValues().get(0));
        assertEquals(portfolioValue2, collection.getValues().get(1));
        assertEquals(getDeltas(-1010, -50f), collection.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<PortfolioValue> results = portfolioValueDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValue portfolioValue1 = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValue portfolioValue2 = new PortfolioValue().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setCredits(20).setMarketValues(marketValues).setTotal(70);
        assertEquals(1, portfolioValueDB.add(portfolioValue1));
        assertEquals(1, portfolioValueDB.add(portfolioValue2));

        Results<PortfolioValue> results = portfolioValueDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValue1, results.getResults().get(0));
        assertEquals(portfolioValue2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValue portfolioValue1 = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValue portfolioValue2 = new PortfolioValue().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setCredits(20).setMarketValues(marketValues).setTotal(70);
        assertEquals(1, portfolioValueDB.add(portfolioValue1));
        assertEquals(1, portfolioValueDB.add(portfolioValue2));

        Set<Sort> sort = new LinkedHashSet<>(asList(CREDITS.toSort(DESC), USER_ID.toSort(DESC)));
        Results<PortfolioValue> results = portfolioValueDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValue2, results.getResults().get(0));
        assertEquals(portfolioValue1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<PortfolioValue> list = new ArrayList<>();
        assertEquals(0, portfolioValueDB.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValue portfolioValue1 = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValue portfolioValue2 = new PortfolioValue().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setCredits(20).setMarketValues(marketValues).setTotal(70);
        assertEquals(1, portfolioValueDB.add(portfolioValue1));
        assertEquals(1, portfolioValueDB.add(portfolioValue2));

        List<PortfolioValue> results = new ArrayList<>();
        assertEquals(2, portfolioValueDB.consume(results::add, emptySet()));
        assertEquals(2, results.size());
        assertEquals(portfolioValue1, results.get(0));
        assertEquals(portfolioValue2, results.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValue portfolioValue1 = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValue portfolioValue2 = new PortfolioValue().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setCredits(20).setMarketValues(marketValues).setTotal(68);
        assertEquals(1, portfolioValueDB.add(portfolioValue1));
        assertEquals(1, portfolioValueDB.add(portfolioValue2));

        List<PortfolioValue> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(CREDITS.toSort(DESC), USER_ID.toSort(DESC)));
        assertEquals(2, portfolioValueDB.consume(results::add, sort));
        assertEquals(2, results.size());
        assertEquals(portfolioValue2, results.get(0));
        assertEquals(portfolioValue1, results.get(1));
    }

    @Test
    public void testAdd() {
        PortfolioValue portfolioValue = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(singletonMap(TWITTER, 1000L)).setTotal(1010);
        assertEquals(1, portfolioValueDB.add(portfolioValue));
    }

    @Test
    public void testAddConflictSameValues() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValue portfolioValue = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        assertEquals(1, portfolioValueDB.add(portfolioValue));
        assertEquals(0, portfolioValueDB.add(portfolioValue));

        List<PortfolioValue> results = new ArrayList<>();
        assertEquals(1, portfolioValueDB.consume(results::add, emptySet()));
        assertEquals(1, results.size());
        assertEquals(portfolioValue, results.get(0));
    }

    @Test
    public void testAddConflictDifferentValues() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValue portfolioValue = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        assertEquals(1, portfolioValueDB.add(portfolioValue));
        portfolioValue.setCredits(12);
        portfolioValue.setTotal(1012);
        assertEquals(1, portfolioValueDB.add(portfolioValue));

        List<PortfolioValue> results = new ArrayList<>();
        assertEquals(1, portfolioValueDB.consume(results::add, emptySet()));
        assertEquals(1, results.size());
        assertEquals(portfolioValue, results.get(0));
    }

    @Test
    public void testAddAll() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValue portfolioValue1 = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValue portfolioValue2 = new PortfolioValue().setUserId(user2.getId()).setTimestamp(now.minusSeconds(10)).setCredits(20).setMarketValues(marketValues).setTotal(70);
        assertEquals(2, portfolioValueDB.addAll(asList(portfolioValue1, portfolioValue2)));

        List<PortfolioValue> results = new ArrayList<>();
        assertEquals(2, portfolioValueDB.consume(results::add, emptySet()));
        assertEquals(2, results.size());
        assertEquals(portfolioValue1, results.get(0));
        assertEquals(portfolioValue2, results.get(1));
    }

    @Test
    public void testAgeOff() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValue portfolioValue1 = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValue portfolioValue2 = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now.minusSeconds(10)).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValue portfolioValue3 = new PortfolioValue().setUserId(user1.getId()).setTimestamp(now.minusSeconds(20)).setCredits(10).setMarketValues(marketValues).setTotal(60);

        assertEquals(1, portfolioValueDB.add(portfolioValue1));
        assertEquals(1, portfolioValueDB.add(portfolioValue2));
        assertEquals(1, portfolioValueDB.add(portfolioValue3));
        assertEquals(2, portfolioValueDB.ageOff(now.minusSeconds(5)));

        List<PortfolioValue> results = new ArrayList<>();
        assertEquals(1, portfolioValueDB.consume(results::add, emptySet()));
        assertEquals(1, results.size());
        assertEquals(portfolioValue1, results.get(0));
    }
}
