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
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.stream;
import static java.util.Collections.*;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.CREDITS;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class JdbcPortfolioValueSummaryDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserDB userTable;
    private StockDB stockTable;
    private StockPriceDB stockPriceTable;
    private UserCreditsDB userCreditsTable;
    private UserStockDB userStockTable;
    private PortfolioValueSummaryTable portfolioValueSummaryTable;

    private JdbcPortfolioValueSummaryDB portfolioValueSummaryDB;

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
        portfolioValueSummaryTable = new PortfolioValueSummaryTable();
        portfolioValueSummaryDB = new JdbcPortfolioValueSummaryDB(dataSourceExternalResource.get());

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
            portfolioValueSummaryTable.truncate(connection);
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
    public void testGenerateMissing() {
        PortfolioValueSummary portfolioValueSummary = portfolioValueSummaryDB.generate();
        assertEquals(0, portfolioValueSummary.getCredits());
        assertEquals(Market.values().length, portfolioValueSummary.getMarketValues().size());
        assertEquals(0, portfolioValueSummary.getMarketValues().values().stream().mapToLong(l -> l).sum());
        assertEquals(0, portfolioValueSummary.getTotal());
    }

    @Test
    public void testGenerateExistsNoCreditsOrStocks() {
        PortfolioValueSummary portfolioValueSummary = portfolioValueSummaryDB.generate();
        assertEquals(0, portfolioValueSummary.getCredits());
        assertEquals(Market.values().length, portfolioValueSummary.getMarketValues().size());
        assertEquals(0, portfolioValueSummary.getMarketValues().values().stream().mapToLong(l -> l).sum());
        assertEquals(0, portfolioValueSummary.getTotal());
    }

    @Test
    public void testGenerateExistsWithCreditsNoStocks() throws SQLException {
        UserCredits userCredits = new UserCredits().setUserId(user1.getId()).setCredits(10000);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userCreditsTable.add(connection, userCredits));
            connection.commit();
        }

        PortfolioValueSummary fetched = portfolioValueSummaryDB.generate();
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

        PortfolioValueSummary fetched = portfolioValueSummaryDB.generate();
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

        PortfolioValueSummary fetched = portfolioValueSummaryDB.generate();
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
    public void testGetLatestMissing() {
        PortfolioValueSummaryCollection fetched = portfolioValueSummaryDB.getLatest();
        assertTrue(fetched.getSummaries().isEmpty());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestSingleExists() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary().setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary));

        PortfolioValueSummaryCollection fetched = portfolioValueSummaryDB.getLatest();
        assertEquals(1, fetched.getSummaries().size());
        assertEquals(portfolioValueSummary, fetched.getSummaries().iterator().next());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestMultipleExists() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValueSummary portfolioValueSummary1 = new PortfolioValueSummary().setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValueSummary portfolioValueSummary2 = new PortfolioValueSummary().setTimestamp(now.minusSeconds(10)).setCredits(8).setMarketValues(marketValues).setTotal(58);
        PortfolioValueSummary portfolioValueSummary3 = new PortfolioValueSummary().setTimestamp(now.minusSeconds(20)).setCredits(6).setMarketValues(marketValues).setTotal(56);
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary1));
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary2));
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary3));

        PortfolioValueSummaryCollection fetched = portfolioValueSummaryDB.getLatest();
        assertEquals(3, fetched.getSummaries().size());
        assertEquals(portfolioValueSummary1, fetched.getSummaries().get(0));
        assertEquals(portfolioValueSummary2, fetched.getSummaries().get(1));
        assertEquals(portfolioValueSummary3, fetched.getSummaries().get(2));
        assertEquals(getDeltas(4, 7.1428576f), fetched.getDeltas());
    }

    @Test
    public void testGetAllNone() {
        Results<PortfolioValueSummary> results = portfolioValueSummaryDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValueSummary portfolioValueSummary1 = new PortfolioValueSummary().setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValueSummary portfolioValueSummary2 = new PortfolioValueSummary().setTimestamp(now.minusSeconds(10)).setCredits(20).setMarketValues(marketValues).setTotal(70);
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary1));
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary2));

        Results<PortfolioValueSummary> results = portfolioValueSummaryDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValueSummary1, results.getResults().get(0));
        assertEquals(portfolioValueSummary2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValueSummary portfolioValueSummary1 = new PortfolioValueSummary().setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValueSummary portfolioValueSummary2 = new PortfolioValueSummary().setTimestamp(now.minusSeconds(10)).setCredits(20).setMarketValues(marketValues).setTotal(70);
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary1));
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary2));

        Set<Sort> sort = singleton(CREDITS.toSort(DESC));
        Results<PortfolioValueSummary> results = portfolioValueSummaryDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(portfolioValueSummary2, results.getResults().get(0));
        assertEquals(portfolioValueSummary1, results.getResults().get(1));
    }

    @Test
    public void testAdd() {
        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary().setTimestamp(now).setCredits(10).setMarketValues(singletonMap(TWITTER, 1000L)).setTotal(1010);
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary));
    }

    @Test
    public void testAddConflictSameValues() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary().setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary));
        assertEquals(0, portfolioValueSummaryDB.add(portfolioValueSummary));

        PortfolioValueSummaryCollection fetched = portfolioValueSummaryDB.getLatest();
        assertEquals(1, fetched.getSummaries().size());
        assertEquals(portfolioValueSummary, fetched.getSummaries().iterator().next());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testAddConflictDifferentValues() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValueSummary portfolioValueSummary = new PortfolioValueSummary().setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary));
        portfolioValueSummary.setCredits(12);
        portfolioValueSummary.setTotal(1012);
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary));

        PortfolioValueSummaryCollection fetched = portfolioValueSummaryDB.getLatest();
        assertEquals(1, fetched.getSummaries().size());
        assertEquals(portfolioValueSummary, fetched.getSummaries().iterator().next());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testAgeOff() {
        Map<Market, Long> marketValues = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> marketValues.put(market, 10L));

        PortfolioValueSummary portfolioValueSummary1 = new PortfolioValueSummary().setTimestamp(now).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValueSummary portfolioValueSummary2 = new PortfolioValueSummary().setTimestamp(now.minusSeconds(10)).setCredits(10).setMarketValues(marketValues).setTotal(60);
        PortfolioValueSummary portfolioValueSummary3 = new PortfolioValueSummary().setTimestamp(now.minusSeconds(20)).setCredits(10).setMarketValues(marketValues).setTotal(60);

        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary1));
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary2));
        assertEquals(1, portfolioValueSummaryDB.add(portfolioValueSummary3));
        assertEquals(2, portfolioValueSummaryDB.ageOff(now.minusSeconds(5)));

        PortfolioValueSummaryCollection fetched = portfolioValueSummaryDB.getLatest();
        assertEquals(1, fetched.getSummaries().size());
        assertEquals(portfolioValueSummary1, fetched.getSummaries().iterator().next());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }
}
