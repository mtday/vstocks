package vstocks.service.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.service.DataSourceExternalResource;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;

public class JdbcStockStoreIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private MarketTable marketStore;
    private StockTable stockStore;

    private final Market market1 = new Market().setId("id1").setName("name1");
    private final Market market2 = new Market().setId("id2").setName("name2");

    @Before
    public void setup() throws SQLException {
        marketStore = new MarketTable();
        stockStore = new StockTable();

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketStore.add(connection, market1));
            assertEquals(1, marketStore.add(connection, market2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockStore.truncate(connection);
            marketStore.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(stockStore.get(connection, "missing-id").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("symbol").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockStore.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> fetched = stockStore.get(connection, stock.getId());
            assertTrue(fetched.isPresent());
            assertEquals(stock, fetched.get());
        }
    }

    @Test
    public void testGetForMarket() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockStore.getForMarket(connection, market1.getId(), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForMarketSome() throws SQLException {
        Stock stock1 = new Stock().setId("id1").setMarketId(market1.getId()).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarketId(market1.getId()).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockStore.add(connection, stock1));
            assertEquals(1, stockStore.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockStore.getForMarket(connection, market1.getId(), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(stock1));
            assertTrue(results.getResults().contains(stock2));
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        Stock stock1 = new Stock().setId("id1").setMarketId(market1.getId()).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarketId(market2.getId()).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockStore.add(connection, stock1));
            assertEquals(1, stockStore.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockStore.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(stock1));
            assertTrue(results.getResults().contains(stock2));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockStore.add(connection, stock));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockStore.add(connection, stock));
            stockStore.add(connection, stock);
            connection.commit();
        }
    }

    @Test
    public void testUpdateMissing() throws SQLException {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, stockStore.update(connection, stock));
        }
    }

    @Test
    public void testUpdate() throws SQLException {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockStore.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stock.setSymbol("updated");
            stock.setName("updated");
            assertEquals(1, stockStore.update(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> updated = stockStore.get(connection, stock.getId());
            assertTrue(updated.isPresent());
            assertEquals(stock, updated.get());
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, stockStore.delete(connection, "missing-id"));
        }
    }

    @Test
    public void testDelete() throws SQLException {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("sym").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockStore.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockStore.delete(connection, stock.getId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(stockStore.get(connection, stock.getId()).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        Stock stock1 = new Stock().setId("id1").setMarketId(market1.getId()).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarketId(market2.getId()).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockStore.add(connection, stock1));
            assertEquals(1, stockStore.add(connection, stock2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(2, stockStore.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockStore.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
