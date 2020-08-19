package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.ActivityLogTable;
import vstocks.db.jdbc.table.StockTable;
import vstocks.db.jdbc.table.TransactionSummaryTable;
import vstocks.db.jdbc.table.UserTable;
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
import static vstocks.model.ActivityType.STOCK_BUY;
import static vstocks.model.DatabaseField.TOTAL;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class JdbcTransactionSummaryDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private ActivityLogTable activityLogTable;
    private UserTable userTable;
    private StockTable stockTable;
    private TransactionSummaryTable transactionSummaryTable;

    private JdbcTransactionSummaryDB transactionSummaryDB;

    private final Instant now = Instant.now().truncatedTo(SECONDS);
    private final User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
    private final Stock twitterStock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setProfileImage("link");
    private final Stock youtubeStock = new Stock().setMarket(YOUTUBE).setSymbol("sym1").setName("name1").setProfileImage("link");

    @Before
    public void setup() throws SQLException {
        activityLogTable = new ActivityLogTable();
        userTable = new UserTable();
        stockTable = new StockTable();
        transactionSummaryTable = new TransactionSummaryTable();
        transactionSummaryDB = new JdbcTransactionSummaryDB(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            assertEquals(1, stockTable.add(connection, twitterStock));
            assertEquals(1, stockTable.add(connection, youtubeStock));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            transactionSummaryTable.truncate(connection);
            activityLogTable.truncate(connection);
            stockTable.truncate(connection);
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
        TransactionSummary transactionSummary = transactionSummaryDB.generate();
        assertEquals(Market.values().length, transactionSummary.getTransactions().size());
        assertEquals(0, transactionSummary.getTransactions().values().stream().mapToLong(l -> l).sum());
        assertEquals(0, transactionSummary.getTotal());
        assertNull(transactionSummary.getDeltas());
    }

    @Test
    public void testGenerateExists() throws SQLException {
        ActivityLog activityLog1 = new ActivityLog().setId("1").setUserId(user1.getId()).setType(STOCK_BUY).setTimestamp(now).setMarket(twitterStock.getMarket()).setSymbol(twitterStock.getSymbol()).setShares(10).setPrice(10L);
        ActivityLog activityLog2 = new ActivityLog().setId("2").setUserId(user2.getId()).setType(STOCK_BUY).setTimestamp(now).setMarket(youtubeStock.getMarket()).setSymbol(youtubeStock.getSymbol()).setShares(10).setPrice(10L);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, activityLogTable.add(connection, activityLog1));
            assertEquals(1, activityLogTable.add(connection, activityLog2));
            connection.commit();
        }

        TransactionSummary fetched = transactionSummaryDB.generate();
        assertEquals(Market.values().length, fetched.getTransactions().size());
        assertEquals(2, fetched.getTransactions().values().stream().mapToLong(l -> l).sum());
        assertEquals(2, fetched.getTotal());
        assertNull(fetched.getDeltas());
    }

    @Test
    public void testGetLatestMissing() {
        TransactionSummary fetched = transactionSummaryDB.getLatest();
        assertNotNull(fetched.getTimestamp());
        assertEquals(Market.values().length, fetched.getTransactions().size());
        assertEquals(0, fetched.getTransactions().values().stream().mapToLong(l -> l).sum());
        assertEquals(0, fetched.getTotal());
        assertEquals(getDeltas(0, 0f), fetched.getDeltas());
    }

    @Test
    public void testGetLatestSingleExists() {
        Map<Market, Long> transactions = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> transactions.put(market, 10L));

        TransactionSummary transactionSummary = new TransactionSummary().setTimestamp(now).setTransactions(transactions).setTotal(60).setDeltas(getDeltas(0, 0f));
        assertEquals(1, transactionSummaryDB.add(transactionSummary));

        TransactionSummary fetched = transactionSummaryDB.getLatest();
        assertEquals(transactionSummary, fetched);
    }

    @Test
    public void testGetLatestMultipleExists() {
        Map<Market, Long> transactions = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> transactions.put(market, 10L));

        TransactionSummary transactionSummary1 = new TransactionSummary().setTimestamp(now).setTransactions(transactions).setTotal(60).setDeltas(getDeltas(4, 7.1428576f));
        TransactionSummary transactionSummary2 = new TransactionSummary().setTimestamp(now.minusSeconds(10)).setTransactions(transactions).setTotal(58);
        TransactionSummary transactionSummary3 = new TransactionSummary().setTimestamp(now.minusSeconds(20)).setTransactions(transactions).setTotal(56);
        assertEquals(1, transactionSummaryDB.add(transactionSummary1));
        assertEquals(1, transactionSummaryDB.add(transactionSummary2));
        assertEquals(1, transactionSummaryDB.add(transactionSummary3));

        TransactionSummary fetched = transactionSummaryDB.getLatest();
        assertEquals(transactionSummary1, fetched);
    }

    @Test
    public void testGetAllNone() {
        Results<TransactionSummary> results = transactionSummaryDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Map<Market, Long> transactions = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> transactions.put(market, 10L));

        TransactionSummary transactionSummary1 = new TransactionSummary().setTimestamp(now).setTransactions(transactions).setTotal(60);
        TransactionSummary transactionSummary2 = new TransactionSummary().setTimestamp(now.minusSeconds(10)).setTransactions(transactions).setTotal(70);
        assertEquals(1, transactionSummaryDB.add(transactionSummary1));
        assertEquals(1, transactionSummaryDB.add(transactionSummary2));

        Results<TransactionSummary> results = transactionSummaryDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(transactionSummary1, results.getResults().get(0));
        assertEquals(transactionSummary2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        Map<Market, Long> transactions = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> transactions.put(market, 10L));

        TransactionSummary transactionSummary1 = new TransactionSummary().setTimestamp(now).setTransactions(transactions).setTotal(60);
        TransactionSummary transactionSummary2 = new TransactionSummary().setTimestamp(now.minusSeconds(10)).setTransactions(transactions).setTotal(70);
        assertEquals(1, transactionSummaryDB.add(transactionSummary1));
        assertEquals(1, transactionSummaryDB.add(transactionSummary2));

        Set<Sort> sort = singleton(TOTAL.toSort(DESC));
        Results<TransactionSummary> results = transactionSummaryDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(transactionSummary2, results.getResults().get(0));
        assertEquals(transactionSummary1, results.getResults().get(1));
    }

    @Test
    public void testAdd() {
        TransactionSummary transactionSummary = new TransactionSummary().setTimestamp(now).setTransactions(singletonMap(TWITTER, 1000L)).setTotal(1010);
        assertEquals(1, transactionSummaryDB.add(transactionSummary));
    }

    @Test
    public void testAddConflictSameValues() {
        Map<Market, Long> transactions = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> transactions.put(market, 10L));

        TransactionSummary transactionSummary = new TransactionSummary().setTimestamp(now).setTransactions(transactions).setTotal(60).setDeltas(getDeltas(0, 0f));
        assertEquals(1, transactionSummaryDB.add(transactionSummary));
        assertEquals(0, transactionSummaryDB.add(transactionSummary));

        TransactionSummary fetched = transactionSummaryDB.getLatest();
        assertEquals(transactionSummary, fetched);
    }

    @Test
    public void testAddConflictDifferentValues() {
        Map<Market, Long> transactions = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> transactions.put(market, 10L));

        TransactionSummary transactionSummary = new TransactionSummary().setTimestamp(now).setTransactions(transactions).setTotal(60).setDeltas(getDeltas(0, 0f));
        assertEquals(1, transactionSummaryDB.add(transactionSummary));
        transactionSummary.setTotal(1012);
        assertEquals(1, transactionSummaryDB.add(transactionSummary));

        TransactionSummary fetched = transactionSummaryDB.getLatest();
        assertEquals(transactionSummary, fetched);
    }

    @Test
    public void testAgeOff() {
        Map<Market, Long> transactions = new TreeMap<>();
        Arrays.stream(Market.values()).forEach(market -> transactions.put(market, 10L));

        TransactionSummary transactionSummary1 = new TransactionSummary().setTimestamp(now).setTransactions(transactions).setTotal(60);
        TransactionSummary transactionSummary2 = new TransactionSummary().setTimestamp(now.minusSeconds(10)).setTransactions(transactions).setTotal(60);
        TransactionSummary transactionSummary3 = new TransactionSummary().setTimestamp(now.minusSeconds(20)).setTransactions(transactions).setTotal(60);

        assertEquals(1, transactionSummaryDB.add(transactionSummary1));
        assertEquals(1, transactionSummaryDB.add(transactionSummary2));
        assertEquals(1, transactionSummaryDB.add(transactionSummary3));
        assertEquals(2, transactionSummaryDB.ageOff(now.minusSeconds(5)));

        Results<TransactionSummary> results = transactionSummaryDB.getAll(new Page(), emptySet());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(transactionSummary1, results.getResults().iterator().next());
    }
}
