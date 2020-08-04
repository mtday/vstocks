package vstocks.service.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.service.db.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcStockTableIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private StockTable stockTable;

    @Before
    public void setup() {
        stockTable = new StockTable();
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(stockTable.get(connection, TWITTER, "missing").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> fetched = stockTable.get(connection, TWITTER, stock.getSymbol());
            assertTrue(fetched.isPresent());
            assertEquals(stock, fetched.get()); // only compares id
        }
    }

    @Test
    public void testGetForMarket() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getForMarket(connection, TWITTER, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForMarketSomeNoSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getForMarket(connection, TWITTER, new Page(), emptySet());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(stock1, results.getResults().get(0));
            assertEquals(stock2, results.getResults().get(1));
        }
    }

    @Test
    public void testGetForMarketSomeWithSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
            Results<Stock> results = stockTable.getForMarket(connection, TWITTER, new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(stock2, results.getResults().get(0));
            assertEquals(stock1, results.getResults().get(1));
        }
    }

    @Test
    public void testConsumeForMarket() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> results = new ArrayList<>();
            assertEquals(0, stockTable.consumeForMarket(connection, TWITTER, results::add, emptySet()));
            assertTrue(results.isEmpty());
        }
    }

    @Test
    public void testConsumeForMarketSomeNoSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> results = new ArrayList<>();
            assertEquals(2, stockTable.consumeForMarket(connection, TWITTER, results::add, emptySet()));
            assertEquals(2, results.size());
            assertEquals(stock1, results.get(0));
            assertEquals(stock2, results.get(1));
        }
    }

    @Test
    public void testConsumeForMarketSomeWithSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> results = new ArrayList<>();
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
            assertEquals(2, stockTable.consumeForMarket(connection, TWITTER, results::add, sort));
            assertEquals(2, results.size());
            assertEquals(stock2, results.get(0));
            assertEquals(stock1, results.get(1));
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getAll(connection, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSomeNoSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(Market.YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getAll(connection, new Page(), emptySet());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(stock1, results.getResults().get(0));
            assertEquals(stock2, results.getResults().get(1));
        }
    }

    @Test
    public void testGetAllSomeWithSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(Market.YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
            Results<Stock> results = stockTable.getAll(connection, new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(stock2, results.getResults().get(0));
            assertEquals(stock1, results.getResults().get(1));
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> list = new ArrayList<>();
            assertEquals(0, stockTable.consume(connection, list::add, emptySet()));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSomeNoSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(Market.YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> list = new ArrayList<>();
            assertEquals(2, stockTable.consume(connection, list::add, emptySet()));
            assertEquals(2, list.size());
            assertEquals(stock1, list.get(0));
            assertEquals(stock2, list.get(1));
        }
    }

    @Test
    public void testConsumeSomeWithSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(Market.YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> list = new ArrayList<>();
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
            assertEquals(2, stockTable.consume(connection, list::add, sort));
            assertEquals(2, list.size());
            assertEquals(stock2, list.get(0));
            assertEquals(stock1, list.get(1));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
    }

    @Test
    public void testAddConflict() throws SQLException {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, stockTable.add(connection, stock));
            connection.commit();
        }
    }

    @Test
    public void testUpdateMissing() throws SQLException {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, stockTable.update(connection, stock));
        }
    }

    @Test
    public void testUpdateNoChange() throws SQLException {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, stockTable.update(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> updated = stockTable.get(connection, TWITTER, stock.getSymbol());
            assertTrue(updated.isPresent());
            assertEquals(stock.getSymbol(), updated.get().getSymbol());
            assertEquals(stock.getName(), updated.get().getName());
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stock.setName("updated");
            assertEquals(1, stockTable.update(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> updated = stockTable.get(connection, TWITTER, stock.getSymbol());
            assertTrue(updated.isPresent());
            assertEquals(stock.getSymbol(), updated.get().getSymbol());
            assertEquals(stock.getName(), updated.get().getName());
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, stockTable.delete(connection, TWITTER, "missing"));
        }
    }

    @Test
    public void testDelete() throws SQLException {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.delete(connection, TWITTER, stock.getSymbol()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(stockTable.get(connection, TWITTER, stock.getSymbol()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(Market.YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(2, stockTable.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getAll(connection, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
