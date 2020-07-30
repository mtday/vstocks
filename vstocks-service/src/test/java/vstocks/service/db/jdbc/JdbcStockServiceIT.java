package vstocks.service.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.service.db.DataSourceExternalResource;
import vstocks.service.db.jdbc.table.StockTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;

public class JdbcStockServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private StockTable stockTable;
    private JdbcStockService stockService;

    @Before
    public void setup() {
        stockTable = new StockTable();
        stockService = new JdbcStockService(dataSourceExternalResource.get());
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(stockService.get(TWITTER, "missing").isPresent());
    }

    @Test
    public void testGetExists() {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("symbol").setName("name");
        assertEquals(1, stockService.add(stock));

        Optional<Stock> fetched = stockService.get(TWITTER, stock.getId());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
    }

    @Test
    public void testGetForMarket() {
        Results<Stock> results = stockService.getForMarket(TWITTER, new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForMarketSome() {
        Stock stock1 = new Stock().setId("id1").setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarket(TWITTER).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        Results<Stock> results = stockService.getForMarket(TWITTER, new Page());
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
        Stock stock1 = new Stock().setId("id1").setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        Results<Stock> results = stockService.getAll(new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(stock1));
        assertTrue(results.getResults().contains(stock2));
    }

    @Test
    public void testConsumeNone() {
        List<Stock> list = new ArrayList<>();
        assertEquals(0, stockService.consume(list::add));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSome() {
        Stock stock1 = new Stock().setId("id1").setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setId("id2").setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        List<Stock> list = new ArrayList<>();
        assertEquals(2, stockService.consume(list::add));
        assertEquals(2, list.size());
        assertTrue(list.contains(stock1));
        assertTrue(list.contains(stock2));
    }

    @Test
    public void testAdd() {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
        stockService.add(stock);
    }

    @Test
    public void testUpdateMissing() {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(0, stockService.update(stock));
    }

    @Test
    public void testUpdate() {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));

        stock.setSymbol("updated");
        stock.setName("updated");
        assertEquals(1, stockService.update(stock));

        Optional<Stock> updated = stockService.get(TWITTER, stock.getId());
        assertTrue(updated.isPresent());
        assertEquals(stock.getMarket(), updated.get().getMarket());
        assertEquals(stock.getSymbol(), updated.get().getSymbol());
        assertEquals(stock.getName(), updated.get().getName());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, stockService.delete(TWITTER, "missing"));
    }

    @Test
    public void testDelete() {
        Stock stock = new Stock().setId("id").setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
        assertEquals(1, stockService.delete(TWITTER, stock.getId()));
        assertFalse(stockService.get(TWITTER, stock.getId()).isPresent());
    }
}
