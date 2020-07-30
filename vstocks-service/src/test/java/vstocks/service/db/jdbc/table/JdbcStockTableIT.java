package vstocks.service.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.service.db.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.Market.TWITTER;

public class JdbcStockTableIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private StockTable stockTable;

    @Before
    public void setup() throws SQLException {
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
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("symbol").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> fetched = stockTable.get(connection, TWITTER, stock.getId());
            assertTrue(fetched.isPresent());
            assertEquals(stock, fetched.get()); // only compares id
        }
    }

    @Test
    public void testGetForMarket() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getForMarket(connection, TWITTER, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForMarketSome() throws SQLException {
        Stock stock1 = new Stock().setId("id1").setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarket(TWITTER).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getForMarket(connection, TWITTER, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(stock1));
            assertTrue(results.getResults().contains(stock2));
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        Stock stock1 = new Stock().setId("id1").setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarket(Market.YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(stock1));
            assertTrue(results.getResults().contains(stock2));
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> list = new ArrayList<>();
            assertEquals(0, stockTable.consume(connection, list::add));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSome() throws SQLException {
        Stock stock1 = new Stock().setId("id1").setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarket(Market.YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> list = new ArrayList<>();
            assertEquals(2, stockTable.consume(connection, list::add));
            assertEquals(2, list.size());
            assertTrue(list.contains(stock1));
            assertTrue(list.contains(stock2));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            stockTable.add(connection, stock);
            connection.commit();
        }
    }

    @Test
    public void testUpdateMissing() throws SQLException {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, stockTable.update(connection, stock));
        }
    }

    @Test
    public void testUpdateNoChange() throws SQLException {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, stockTable.update(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> updated = stockTable.get(connection, TWITTER, stock.getId());
            assertTrue(updated.isPresent());
            assertEquals(stock.getSymbol(), updated.get().getSymbol());
            assertEquals(stock.getName(), updated.get().getName());
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stock.setSymbol("updated");
            stock.setName("updated");
            assertEquals(1, stockTable.update(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> updated = stockTable.get(connection, TWITTER, stock.getId());
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
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.delete(connection, TWITTER, stock.getId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(stockTable.get(connection, TWITTER, stock.getId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        Stock stock1 = new Stock().setId("id1").setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarket(Market.YOUTUBE).setSymbol("sym2").setName("name2");
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
            Results<Stock> results = stockTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
