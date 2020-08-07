package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.PricedUserStockDB;
import vstocks.db.jdbc.table.StockPriceTable;
import vstocks.db.jdbc.table.StockTable;
import vstocks.db.jdbc.table.UserStockTable;
import vstocks.db.jdbc.table.UserTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcPricedUserStockDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private UserTable userTable;
    private UserStockTable userStockTable;
    private StockTable stockTable;
    private StockPriceTable stockPriceTable;
    private PricedUserStockDB pricedUserStockDb;

    private final User user1 = new User().setEmail("user1@domain.com").setUsername("name1").setDisplayName("Name1");
    private final User user2 = new User().setEmail("user2@domain.com").setUsername("name2").setDisplayName("Name2");
    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        userTable = new UserTable();
        userStockTable = new UserStockTable();
        stockTable = new StockTable();
        stockPriceTable = new StockPriceTable();
        pricedUserStockDb = new JdbcPricedUserStockDB(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userTable.add(connection, user1));
            assertEquals(1, userTable.add(connection, user2));
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockPriceTable.truncate(connection);
            stockTable.truncate(connection);
            userStockTable.truncate(connection);
            userTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(pricedUserStockDb.get("missing-id", TWITTER, "missing-id").isPresent());
    }

    @Test
    public void testGetExistsNoPrice() throws SQLException {
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            connection.commit();
        }

        Optional<PricedUserStock> fetched = pricedUserStockDb.get(userStock.getUserId(), userStock.getMarket(), userStock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(userStock.getUserId(), fetched.get().getUserId());
        assertEquals(userStock.getMarket(), fetched.get().getMarket());
        assertEquals(userStock.getSymbol(), fetched.get().getSymbol());
        assertEquals(userStock.getShares(), fetched.get().getShares());
        assertNotNull(fetched.get().getTimestamp());
        assertEquals(1, fetched.get().getPrice());
    }

    @Test
    public void testGetExistsSinglePrice() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            assertEquals(1, stockPriceTable.add(connection, stockPrice));
            connection.commit();
        }

        Optional<PricedUserStock> fetched = pricedUserStockDb.get(userStock.getUserId(), userStock.getMarket(), userStock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(userStock.getUserId(), fetched.get().getUserId());
        assertEquals(userStock.getMarket(), fetched.get().getMarket());
        assertEquals(userStock.getSymbol(), fetched.get().getSymbol());
        assertEquals(userStock.getShares(), fetched.get().getShares());
        assertEquals(stockPrice.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(stockPrice.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testGetExistsMultiplePrices() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock));
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            connection.commit();
        }

        Optional<PricedUserStock> fetched = pricedUserStockDb.get(userStock.getUserId(), userStock.getMarket(), userStock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(userStock.getUserId(), fetched.get().getUserId());
        assertEquals(userStock.getMarket(), fetched.get().getMarket());
        assertEquals(userStock.getSymbol(), fetched.get().getSymbol());
        assertEquals(userStock.getShares(), fetched.get().getShares());
        assertEquals(stockPrice1.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(stockPrice1.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testGetForUserNone() {
        Results<PricedUserStock> results = pricedUserStockDb.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForUserSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        StockPrice stockPrice11 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice12 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        StockPrice stockPrice21 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice22 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice11));
            assertEquals(1, stockPriceTable.add(connection, stockPrice12));
            assertEquals(1, stockPriceTable.add(connection, stockPrice21));
            assertEquals(1, stockPriceTable.add(connection, stockPrice22));
            connection.commit();
        }

        Results<PricedUserStock> results = pricedUserStockDb.getForUser(user1.getId(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());

        assertEquals(userStock1.getUserId(), results.getResults().get(0).getUserId());
        assertEquals(userStock1.getMarket(), results.getResults().get(0).getMarket());
        assertEquals(userStock1.getSymbol(), results.getResults().get(0).getSymbol());
        assertEquals(userStock1.getShares(), results.getResults().get(0).getShares());
        assertEquals(stockPrice11.getTimestamp(), results.getResults().get(0).getTimestamp());
        assertEquals(stockPrice11.getPrice(), results.getResults().get(0).getPrice());

        assertEquals(userStock2.getUserId(), results.getResults().get(1).getUserId());
        assertEquals(userStock2.getMarket(), results.getResults().get(1).getMarket());
        assertEquals(userStock2.getSymbol(), results.getResults().get(1).getSymbol());
        assertEquals(userStock2.getShares(), results.getResults().get(1).getShares());
        assertEquals(stockPrice21.getTimestamp(), results.getResults().get(1).getTimestamp());
        assertEquals(stockPrice21.getPrice(), results.getResults().get(1).getPrice());
    }

    @Test
    public void testGetForUserSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        StockPrice stockPrice11 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice12 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        StockPrice stockPrice21 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice22 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice11));
            assertEquals(1, stockPriceTable.add(connection, stockPrice12));
            assertEquals(1, stockPriceTable.add(connection, stockPrice21));
            assertEquals(1, stockPriceTable.add(connection, stockPrice22));
            connection.commit();
        }

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), PRICE.toSort()));
        Results<PricedUserStock> results = pricedUserStockDb.getForUser(user1.getId(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());

        assertEquals(userStock2.getUserId(), results.getResults().get(0).getUserId());
        assertEquals(userStock2.getMarket(), results.getResults().get(0).getMarket());
        assertEquals(userStock2.getSymbol(), results.getResults().get(0).getSymbol());
        assertEquals(userStock2.getShares(), results.getResults().get(0).getShares());
        assertEquals(stockPrice21.getTimestamp(), results.getResults().get(0).getTimestamp());
        assertEquals(stockPrice21.getPrice(), results.getResults().get(0).getPrice());

        assertEquals(userStock1.getUserId(), results.getResults().get(1).getUserId());
        assertEquals(userStock1.getMarket(), results.getResults().get(1).getMarket());
        assertEquals(userStock1.getSymbol(), results.getResults().get(1).getSymbol());
        assertEquals(userStock1.getShares(), results.getResults().get(1).getShares());
        assertEquals(stockPrice11.getTimestamp(), results.getResults().get(1).getTimestamp());
        assertEquals(stockPrice11.getPrice(), results.getResults().get(1).getPrice());
    }

    @Test
    public void testGetForStockNone() {
        Results<PricedUserStock> results = pricedUserStockDb.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForStockSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            connection.commit();
        }

        Results<PricedUserStock> results = pricedUserStockDb.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());

        assertEquals(userStock1.getUserId(), results.getResults().get(0).getUserId());
        assertEquals(userStock1.getMarket(), results.getResults().get(0).getMarket());
        assertEquals(userStock1.getSymbol(), results.getResults().get(0).getSymbol());
        assertEquals(userStock1.getShares(), results.getResults().get(0).getShares());
        assertEquals(stockPrice1.getTimestamp(), results.getResults().get(0).getTimestamp());
        assertEquals(stockPrice1.getPrice(), results.getResults().get(0).getPrice());

        assertEquals(userStock2.getUserId(), results.getResults().get(1).getUserId());
        assertEquals(userStock2.getMarket(), results.getResults().get(1).getMarket());
        assertEquals(userStock2.getSymbol(), results.getResults().get(1).getSymbol());
        assertEquals(userStock2.getShares(), results.getResults().get(1).getShares());
        assertEquals(stockPrice1.getTimestamp(), results.getResults().get(1).getTimestamp());
        assertEquals(stockPrice1.getPrice(), results.getResults().get(1).getPrice());
    }

    @Test
    public void testGetForStockSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            connection.commit();
        }

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), SYMBOL.toSort()));
        Results<PricedUserStock> results = pricedUserStockDb.getForStock(TWITTER, stock1.getSymbol(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());

        assertEquals(userStock2.getUserId(), results.getResults().get(0).getUserId());
        assertEquals(userStock2.getMarket(), results.getResults().get(0).getMarket());
        assertEquals(userStock2.getSymbol(), results.getResults().get(0).getSymbol());
        assertEquals(userStock2.getShares(), results.getResults().get(0).getShares());
        assertEquals(stockPrice1.getTimestamp(), results.getResults().get(0).getTimestamp());
        assertEquals(stockPrice1.getPrice(), results.getResults().get(0).getPrice());

        assertEquals(userStock1.getUserId(), results.getResults().get(1).getUserId());
        assertEquals(userStock1.getMarket(), results.getResults().get(1).getMarket());
        assertEquals(userStock1.getSymbol(), results.getResults().get(1).getSymbol());
        assertEquals(userStock1.getShares(), results.getResults().get(1).getShares());
        assertEquals(stockPrice1.getTimestamp(), results.getResults().get(1).getTimestamp());
        assertEquals(stockPrice1.getPrice(), results.getResults().get(1).getPrice());
    }

    @Test
    public void testGetAllNone() {
        Results<PricedUserStock> results = pricedUserStockDb.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        StockPrice stockPrice11 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice12 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        StockPrice stockPrice21 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice22 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice11));
            assertEquals(1, stockPriceTable.add(connection, stockPrice12));
            assertEquals(1, stockPriceTable.add(connection, stockPrice21));
            assertEquals(1, stockPriceTable.add(connection, stockPrice22));
            connection.commit();
        }

        Results<PricedUserStock> results = pricedUserStockDb.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());

        assertEquals(userStock1.getUserId(), results.getResults().get(0).getUserId());
        assertEquals(userStock1.getMarket(), results.getResults().get(0).getMarket());
        assertEquals(userStock1.getSymbol(), results.getResults().get(0).getSymbol());
        assertEquals(userStock1.getShares(), results.getResults().get(0).getShares());
        assertEquals(stockPrice11.getTimestamp(), results.getResults().get(0).getTimestamp());
        assertEquals(stockPrice11.getPrice(), results.getResults().get(0).getPrice());

        assertEquals(userStock2.getUserId(), results.getResults().get(1).getUserId());
        assertEquals(userStock2.getMarket(), results.getResults().get(1).getMarket());
        assertEquals(userStock2.getSymbol(), results.getResults().get(1).getSymbol());
        assertEquals(userStock2.getShares(), results.getResults().get(1).getShares());
        assertEquals(stockPrice21.getTimestamp(), results.getResults().get(1).getTimestamp());
        assertEquals(stockPrice21.getPrice(), results.getResults().get(1).getPrice());
    }

    @Test
    public void testGetAllSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock2.getSymbol()).setShares(10);
        StockPrice stockPrice11 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice12 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        StockPrice stockPrice21 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice22 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice11));
            assertEquals(1, stockPriceTable.add(connection, stockPrice12));
            assertEquals(1, stockPriceTable.add(connection, stockPrice21));
            assertEquals(1, stockPriceTable.add(connection, stockPrice22));
            connection.commit();
        }

        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), SYMBOL.toSort()));
        Results<PricedUserStock> results = pricedUserStockDb.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());

        assertEquals(userStock2.getUserId(), results.getResults().get(0).getUserId());
        assertEquals(userStock2.getMarket(), results.getResults().get(0).getMarket());
        assertEquals(userStock2.getSymbol(), results.getResults().get(0).getSymbol());
        assertEquals(userStock2.getShares(), results.getResults().get(0).getShares());
        assertEquals(stockPrice21.getTimestamp(), results.getResults().get(0).getTimestamp());
        assertEquals(stockPrice21.getPrice(), results.getResults().get(0).getPrice());

        assertEquals(userStock1.getUserId(), results.getResults().get(1).getUserId());
        assertEquals(userStock1.getMarket(), results.getResults().get(1).getMarket());
        assertEquals(userStock1.getSymbol(), results.getResults().get(1).getSymbol());
        assertEquals(userStock1.getShares(), results.getResults().get(1).getShares());
        assertEquals(stockPrice11.getTimestamp(), results.getResults().get(1).getTimestamp());
        assertEquals(stockPrice11.getPrice(), results.getResults().get(1).getPrice());
    }

    @Test
    public void testConsumeNone() {
        List<PricedUserStock> list = new ArrayList<>();
        assertEquals(0, pricedUserStockDb.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            connection.commit();
        }

        List<PricedUserStock> list = new ArrayList<>();
        assertEquals(2, pricedUserStockDb.consume(list::add, emptySet()));
        assertEquals(2, list.size());

        assertEquals(userStock1.getUserId(), list.get(0).getUserId());
        assertEquals(userStock1.getMarket(), list.get(0).getMarket());
        assertEquals(userStock1.getSymbol(), list.get(0).getSymbol());
        assertEquals(userStock1.getShares(), list.get(0).getShares());
        assertEquals(stockPrice1.getTimestamp(), list.get(0).getTimestamp());
        assertEquals(stockPrice1.getPrice(), list.get(0).getPrice());

        assertEquals(userStock2.getUserId(), list.get(1).getUserId());
        assertEquals(userStock2.getMarket(), list.get(1).getMarket());
        assertEquals(userStock2.getSymbol(), list.get(1).getSymbol());
        assertEquals(userStock2.getShares(), list.get(1).getShares());
        assertEquals(stockPrice1.getTimestamp(), list.get(1).getTimestamp());
        assertEquals(stockPrice1.getPrice(), list.get(1).getPrice());
    }

    @Test
    public void testConsumeSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        UserStock userStock1 = new UserStock().setUserId(user1.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        UserStock userStock2 = new UserStock().setUserId(user2.getId()).setMarket(TWITTER).setSymbol(stock1.getSymbol()).setShares(10);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, userStockTable.add(connection, userStock1));
            assertEquals(1, userStockTable.add(connection, userStock2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            connection.commit();
        }

        List<PricedUserStock> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(USER_ID.toSort(DESC), SYMBOL.toSort()));
        assertEquals(2, pricedUserStockDb.consume(list::add, sort));
        assertEquals(2, list.size());

        assertEquals(userStock2.getUserId(), list.get(0).getUserId());
        assertEquals(userStock2.getMarket(), list.get(0).getMarket());
        assertEquals(userStock2.getSymbol(), list.get(0).getSymbol());
        assertEquals(userStock2.getShares(), list.get(0).getShares());
        assertEquals(stockPrice1.getTimestamp(), list.get(0).getTimestamp());
        assertEquals(stockPrice1.getPrice(), list.get(0).getPrice());

        assertEquals(userStock1.getUserId(), list.get(1).getUserId());
        assertEquals(userStock1.getMarket(), list.get(1).getMarket());
        assertEquals(userStock1.getSymbol(), list.get(1).getSymbol());
        assertEquals(userStock1.getShares(), list.get(1).getShares());
        assertEquals(stockPrice1.getTimestamp(), list.get(1).getTimestamp());
        assertEquals(stockPrice1.getPrice(), list.get(1).getPrice());
    }
}
