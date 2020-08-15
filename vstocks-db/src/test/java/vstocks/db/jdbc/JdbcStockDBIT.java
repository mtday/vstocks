package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.Stock;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.StockTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcStockDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private StockTable stockTable;
    private JdbcStockDB stockDB;

    @Before
    public void setup() throws SQLException {
        stockTable = new StockTable();
        stockDB = new JdbcStockDB(dataSourceExternalResource.get());

        // Clean out the stocks added via flyway
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockTable.truncate(connection);
            connection.commit();
        }
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
        assertFalse(stockDB.get(TWITTER, "missing").isPresent());
    }

    @Test
    public void testGetExists() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("name").setImageLink("link");
        assertEquals(1, stockDB.add(stock));

        Optional<Stock> fetched = stockDB.get(TWITTER, stock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertEquals(stock.getImageLink(), fetched.get().getImageLink());
    }

    @Test
    public void testGetForMarketNone() {
        Results<Stock> results = stockDB.getForMarket(TWITTER, new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForMarketSomeNoSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setImageLink("link");
        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));

        Results<Stock> results = stockDB.getForMarket(TWITTER, new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stock1, results.getResults().get(0));
        assertEquals(stock2, results.getResults().get(1));
    }

    @Test
    public void testGetForMarketSomeWithSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setImageLink("link");
        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        Results<Stock> results = stockDB.getForMarket(TWITTER, new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stock2, results.getResults().get(0));
        assertEquals(stock1, results.getResults().get(1));
    }

    @Test
    public void testConsumeForMarketNone() {
        List<Stock> results = new ArrayList<>();
        assertEquals(0, stockDB.consumeForMarket(TWITTER, results::add, emptySet()));
        assertTrue(results.isEmpty());
    }

    @Test
    public void testConsumeForMarketSomeNoSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setImageLink("link");
        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));

        List<Stock> results = new ArrayList<>();
        assertEquals(2, stockDB.consumeForMarket(TWITTER, results::add, emptySet()));
        assertEquals(2, results.size());
        assertEquals(stock1, results.get(0));
        assertEquals(stock2, results.get(1));
    }

    @Test
    public void testConsumeForMarketSomeWithSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setImageLink("link");
        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));

        List<Stock> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        assertEquals(2, stockDB.consumeForMarket(TWITTER, results::add, sort));
        assertEquals(2, results.size());
        assertEquals(stock2, results.get(0));
        assertEquals(stock1, results.get(1));
    }

    @Test
    public void testGetAllNone() {
        Results<Stock> results = stockDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setImageLink("link");
        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));

        Results<Stock> results = stockDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stock1, results.getResults().get(0));
        assertEquals(stock2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setImageLink("link");
        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        Results<Stock> results = stockDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stock2, results.getResults().get(0));
        assertEquals(stock1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<Stock> list = new ArrayList<>();
        assertEquals(0, stockDB.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setImageLink("link");
        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));

        List<Stock> list = new ArrayList<>();
        assertEquals(2, stockDB.consume(list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(stock1, list.get(0));
        assertEquals(stock2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setImageLink("link");
        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));

        List<Stock> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        assertEquals(2, stockDB.consume(list::add, sort));
        assertEquals(2, list.size());
        assertEquals(stock2, list.get(0));
        assertEquals(stock1, list.get(1));
    }

    @Test
    public void testAdd() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setImageLink("link");
        assertEquals(1, stockDB.add(stock));

        Optional<Stock> fetched = stockDB.get(TWITTER, stock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertEquals(stock.getImageLink(), fetched.get().getImageLink());
    }

    @Test
    public void testAddNoImageLink() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockDB.add(stock));

        Optional<Stock> fetched = stockDB.get(TWITTER, stock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertNull(fetched.get().getImageLink());
    }

    @Test
    public void testAddConflictSameName() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setImageLink("link");
        assertEquals(1, stockDB.add(stock));
        assertEquals(0, stockDB.add(stock));

        Optional<Stock> fetched = stockDB.get(TWITTER, stock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertEquals(stock.getImageLink(), fetched.get().getImageLink());
    }

    @Test
    public void testAddConflictDifferentName() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setImageLink("link");
        assertEquals(1, stockDB.add(stock));
        stock.setName("updated");
        assertEquals(1, stockDB.add(stock));

        Optional<Stock> fetched = stockDB.get(TWITTER, stock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
    }

    @Test
    public void testUpdateMissing() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setImageLink("link");
        assertEquals(0, stockDB.update(stock));
    }

    @Test
    public void testUpdate() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setImageLink("link");
        assertEquals(1, stockDB.add(stock));

        stock.setName("updated");
        stock.setImageLink("updated");
        assertEquals(1, stockDB.update(stock));

        Optional<Stock> updated = stockDB.get(TWITTER, stock.getSymbol());
        assertTrue(updated.isPresent());
        assertEquals(stock.getMarket(), updated.get().getMarket());
        assertEquals(stock.getSymbol(), updated.get().getSymbol());
        assertEquals(stock.getName(), updated.get().getName());
        assertEquals(stock.getImageLink(), updated.get().getImageLink());
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, stockDB.delete(TWITTER, "missing"));
    }

    @Test
    public void testDelete() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setImageLink("link");
        assertEquals(1, stockDB.add(stock));
        assertEquals(1, stockDB.delete(TWITTER, stock.getSymbol()));
        assertFalse(stockDB.get(TWITTER, stock.getSymbol()).isPresent());
    }
}
