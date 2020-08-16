package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.StockTable;
import vstocks.db.jdbc.table.UserStockTable;
import vstocks.db.jdbc.table.UserTable;
import vstocks.model.Sort;
import vstocks.model.Stock;
import vstocks.model.User;
import vstocks.model.UserStock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.SortDirection.DESC;
import static vstocks.model.User.generateId;

public class JdbcOwnedStockDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private StockTable stockTable;
    private UserTable userTable;
    private UserStockTable userStockTable;
    private JdbcOwnedStockDB ownedStockDB;
    private JdbcUserStockDB userStockDB;

    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setProfileImage("link");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setProfileImage("link");
    private final Stock stock3 = new Stock().setMarket(YOUTUBE).setSymbol("sym1").setName("name1").setProfileImage("link");
    private final Stock stock4 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setProfileImage("link");
    private final User user1 = new User().setId(generateId("user1@domain.com")).setEmail("user1@domain.com").setUsername("user1").setDisplayName("User 1");
    private final User user2 = new User().setId(generateId("user2@domain.com")).setEmail("user2@domain.com").setUsername("user2").setDisplayName("User 2");
    private final User user3 = new User().setId(generateId("user3@domain.com")).setEmail("user3@domain.com").setUsername("user3").setDisplayName("User 3");

    @Before
    public void setup() throws SQLException {
        stockTable = new StockTable();
        userTable = new UserTable();
        userStockTable = new UserStockTable();
        ownedStockDB = new JdbcOwnedStockDB(dataSourceExternalResource.get());
        userStockDB = new JdbcUserStockDB(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            // Clean out the stocks added via flyway
            stockTable.truncate(connection);

            // Add some stocks and users for testing
            stockTable.add(connection, stock1);
            stockTable.add(connection, stock2);
            stockTable.add(connection, stock3);
            stockTable.add(connection, stock4);
            userTable.add(connection, user1);
            userTable.add(connection, user2);
            userTable.add(connection, user3);
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            userStockTable.truncate(connection);
            stockTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testConsumeForMarketNone() {
        List<Stock> list = new ArrayList<>();
        assertEquals(0, ownedStockDB.consumeForMarket(TWITTER, list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeForMarketSomeNoSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(stock1.getMarket()).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(stock2.getMarket()).setSymbol(stock2.getSymbol()).setShares(10);
        UserStock userStock3 = new UserStock().setUserId(user2.getId()).setMarket(stock2.getMarket()).setSymbol(stock2.getSymbol()).setShares(10);
        UserStock userStock4 = new UserStock().setUserId(user3.getId()).setMarket(stock3.getMarket()).setSymbol(stock3.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));
        assertEquals(1, userStockDB.add(userStock3));
        assertEquals(1, userStockDB.add(userStock4));

        List<Stock> results = new ArrayList<>();
        assertEquals(2, ownedStockDB.consumeForMarket(TWITTER, results::add, emptySet()));
        assertEquals(2, results.size());
        assertEquals(stock1, results.get(0));
        assertEquals(stock2, results.get(1));
    }

    @Test
    public void testConsumeForMarketSomeWithSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(stock1.getMarket()).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(stock2.getMarket()).setSymbol(stock2.getSymbol()).setShares(10);
        UserStock userStock3 = new UserStock().setUserId(user2.getId()).setMarket(stock2.getMarket()).setSymbol(stock2.getSymbol()).setShares(10);
        UserStock userStock4 = new UserStock().setUserId(user3.getId()).setMarket(stock3.getMarket()).setSymbol(stock3.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));
        assertEquals(1, userStockDB.add(userStock3));
        assertEquals(1, userStockDB.add(userStock4));

        List<Stock> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        assertEquals(2, ownedStockDB.consumeForMarket(TWITTER, results::add, sort));
        assertEquals(2, results.size());
        assertEquals(stock2, results.get(0));
        assertEquals(stock1, results.get(1));
    }

    @Test
    public void testConsumeNone() {
        List<Stock> list = new ArrayList<>();
        assertEquals(0, ownedStockDB.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(stock1.getMarket()).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(stock2.getMarket()).setSymbol(stock2.getSymbol()).setShares(10);
        UserStock userStock3 = new UserStock().setUserId(user2.getId()).setMarket(stock2.getMarket()).setSymbol(stock2.getSymbol()).setShares(10);
        UserStock userStock4 = new UserStock().setUserId(user3.getId()).setMarket(stock3.getMarket()).setSymbol(stock3.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));
        assertEquals(1, userStockDB.add(userStock3));
        assertEquals(1, userStockDB.add(userStock4));

        List<Stock> results = new ArrayList<>();
        assertEquals(3, ownedStockDB.consume(results::add, emptySet()));
        assertEquals(3, results.size());
        assertEquals(stock1, results.get(0));
        assertEquals(stock2, results.get(1));
        assertEquals(stock3, results.get(2));
    }

    @Test
    public void testConsumeSomeWithSort() {
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(stock1.getMarket()).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(stock2.getMarket()).setSymbol(stock2.getSymbol()).setShares(10);
        UserStock userStock3 = new UserStock().setUserId(user2.getId()).setMarket(stock2.getMarket()).setSymbol(stock2.getSymbol()).setShares(10);
        UserStock userStock4 = new UserStock().setUserId(user3.getId()).setMarket(stock3.getMarket()).setSymbol(stock3.getSymbol()).setShares(10);
        assertEquals(1, userStockDB.add(userStock1));
        assertEquals(1, userStockDB.add(userStock2));
        assertEquals(1, userStockDB.add(userStock3));
        assertEquals(1, userStockDB.add(userStock4));

        List<Stock> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), MARKET.toSort()));
        assertEquals(3, ownedStockDB.consume(results::add, sort));
        assertEquals(3, results.size());
        assertEquals(stock2, results.get(0));
        assertEquals(stock1, results.get(1));
        assertEquals(stock3, results.get(2));
    }
}
