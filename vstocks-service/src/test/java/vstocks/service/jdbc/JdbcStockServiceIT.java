package vstocks.service.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.service.DataSourceExternalResource;
import vstocks.service.jdbc.table.MarketTable;
import vstocks.service.jdbc.table.StockTable;
import vstocks.model.Market;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static org.junit.Assert.*;

public class JdbcStockServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private MarketTable marketTable;
    private StockTable stockTable;
    private JdbcStockService stockService;

    private final Market market1 = new Market().setId("id1").setName("name1");
    private final Market market2 = new Market().setId("id2").setName("name2");

    @Before
    public void setup() throws SQLException {
        marketTable = new MarketTable();
        stockTable = new StockTable();
        stockService = new JdbcStockService(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market1));
            assertEquals(1, marketTable.add(connection, market2));
            connection.commit();
        }
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockTable.truncate(connection);
            marketTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(stockService.get("missing", "missing").isPresent());
    }

    @Test
    public void testGetExists() {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("symbol").setName("name");
        assertEquals(1, stockService.add(stock));

        Optional<Stock> fetched = stockService.get(market1.getId(), stock.getId());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarketId(), fetched.get().getMarketId());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
    }

    @Test
    public void testGetForMarket() {
        Results<Stock> results = stockService.getForMarket(market1.getId(), new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForMarketSome() {
        Stock stock1 = new Stock().setId("id1").setMarketId(market1.getId()).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarketId(market1.getId()).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        Results<Stock> results = stockService.getForMarket(market1.getId(), new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(stock1));
        assertTrue(results.getResults().contains(stock2));
    }

    @Test
    public void testGetAllNone() {
        Results<Stock> results = stockService.getAll(new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSome() {
        Stock stock1 = new Stock().setId("id1").setMarketId(market1.getId()).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarketId(market2.getId()).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        Results<Stock> results = stockService.getAll(new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(stock1));
        assertTrue(results.getResults().contains(stock2));
    }

    @Test
    public void testAdd() {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
        stockService.add(stock);
    }

    @Test
    public void testUpdateMissing() {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("sym").setName("name");
        assertEquals(0, stockService.update(stock));
    }

    @Test
    public void testUpdate() {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));

        stock.setSymbol("updated");
        stock.setName("updated");
        assertEquals(1, stockService.update(stock));

        Optional<Stock> updated = stockService.get(market1.getId(), stock.getId());
        assertTrue(updated.isPresent());
        assertEquals(stock.getMarketId(), updated.get().getMarketId());
        assertEquals(stock.getSymbol(), updated.get().getSymbol());
        assertEquals(stock.getName(), updated.get().getName());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, stockService.delete("missing", "missing"));
    }

    @Test
    public void testDelete() {
        Stock stock = new Stock().setId("id").setMarketId(market1.getId()).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
        assertEquals(1, stockService.delete(market1.getId(), stock.getId()));
        assertFalse(stockService.get(market1.getId(), stock.getId()).isPresent());
    }
}
