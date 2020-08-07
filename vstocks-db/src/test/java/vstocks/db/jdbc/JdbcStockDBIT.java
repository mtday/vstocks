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
    private JdbcStockDB stockService;

    @Before
    public void setup() throws SQLException {
        stockTable = new StockTable();
        stockService = new JdbcStockDB(dataSourceExternalResource.get());

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
        assertFalse(stockService.get(TWITTER, "missing", null).isPresent());
    }

    @Test
    public void testGetExistsActive() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("name");
        assertEquals(1, stockService.add(stock));

        Optional<Stock> nullFetched = stockService.get(TWITTER, stock.getSymbol(), null);
        assertTrue(nullFetched.isPresent());
        assertEquals(stock.getMarket(), nullFetched.get().getMarket());
        assertEquals(stock.getSymbol(), nullFetched.get().getSymbol());
        assertEquals(stock.getName(), nullFetched.get().getName());
        assertEquals(stock.isActive(), nullFetched.get().isActive());

        Optional<Stock> trueFetched = stockService.get(TWITTER, stock.getSymbol(), true);
        assertTrue(trueFetched.isPresent());
        assertEquals(stock.getMarket(), trueFetched.get().getMarket());
        assertEquals(stock.getSymbol(), trueFetched.get().getSymbol());
        assertEquals(stock.getName(), trueFetched.get().getName());
        assertEquals(stock.isActive(), trueFetched.get().isActive());

        assertFalse(stockService.get(TWITTER, stock.getSymbol(), false).isPresent());
    }

    @Test
    public void testGetForMarket() {
        Results<Stock> results = stockService.getForMarket(TWITTER, null, new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForMarketSomeNoSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        Results<Stock> results = stockService.getForMarket(TWITTER, null, new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stock1, results.getResults().get(0));
        assertEquals(stock2, results.getResults().get(1));
    }

    @Test
    public void testGetForMarketSomeWithSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        Results<Stock> results = stockService.getForMarket(TWITTER, null, new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stock2, results.getResults().get(0));
        assertEquals(stock1, results.getResults().get(1));
    }

    @Test
    public void testGetForMarketMixedActive() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(TWITTER).setSymbol("sym3").setName("name3").setActive(false);
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));

        Results<Stock> nullResults = stockService.getForMarket(TWITTER, null, new Page(), emptySet());
        assertEquals(3, nullResults.getTotal());
        assertEquals(3, nullResults.getResults().size());

        Results<Stock> trueResults = stockService.getForMarket(TWITTER, true, new Page(), emptySet());
        assertEquals(2, trueResults.getTotal());
        assertEquals(2, trueResults.getResults().size());

        Results<Stock> falseResults = stockService.getForMarket(TWITTER, false, new Page(), emptySet());
        assertEquals(1, falseResults.getTotal());
        assertEquals(1, falseResults.getResults().size());
    }

    @Test
    public void testConsumeForMarket() {
        List<Stock> results = new ArrayList<>();
        assertEquals(0, stockService.consumeForMarket(TWITTER, null, results::add, emptySet()));
        assertTrue(results.isEmpty());
    }

    @Test
    public void testConsumeForMarketSomeNoSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        List<Stock> results = new ArrayList<>();
        assertEquals(2, stockService.consumeForMarket(TWITTER, null, results::add, emptySet()));
        assertEquals(2, results.size());
        assertEquals(stock1, results.get(0));
        assertEquals(stock2, results.get(1));
    }

    @Test
    public void testConsumeForMarketSomeWithSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        List<Stock> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        assertEquals(2, stockService.consumeForMarket(TWITTER, null, results::add, sort));
        assertEquals(2, results.size());
        assertEquals(stock2, results.get(0));
        assertEquals(stock1, results.get(1));
    }

    @Test
    public void testConsumeForMarketMixedActive() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(TWITTER).setSymbol("sym3").setName("name3").setActive(false);
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));

        List<Stock> nullResults = new ArrayList<>();
        assertEquals(3, stockService.consumeForMarket(TWITTER, null, nullResults::add, emptySet()));
        assertEquals(3, nullResults.size());

        List<Stock> trueResults = new ArrayList<>();
        assertEquals(2, stockService.consumeForMarket(TWITTER, true, trueResults::add, emptySet()));
        assertEquals(2, trueResults.size());

        List<Stock> falseResults = new ArrayList<>();
        assertEquals(1, stockService.consumeForMarket(TWITTER, false, falseResults::add, emptySet()));
        assertEquals(1, falseResults.size());
    }

    @Test
    public void testGetAllNone() {
        Results<Stock> results = stockService.getAll(null, new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        Results<Stock> results = stockService.getAll(null, new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stock1, results.getResults().get(0));
        assertEquals(stock2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        Results<Stock> results = stockService.getAll(null, new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stock2, results.getResults().get(0));
        assertEquals(stock1, results.getResults().get(1));
    }

    @Test
    public void testGetAllMixedActive() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(YOUTUBE).setSymbol("sym3").setName("name3").setActive(false);
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));

        Results<Stock> nullResults = stockService.getAll(null, new Page(), emptySet());
        assertEquals(3, nullResults.getTotal());
        assertEquals(3, nullResults.getResults().size());

        Results<Stock> trueResults = stockService.getAll(true, new Page(), emptySet());
        assertEquals(2, trueResults.getTotal());
        assertEquals(2, trueResults.getResults().size());

        Results<Stock> falseResults = stockService.getAll(false, new Page(), emptySet());
        assertEquals(1, falseResults.getTotal());
        assertEquals(1, falseResults.getResults().size());
    }

    @Test
    public void testConsumeNone() {
        List<Stock> list = new ArrayList<>();
        assertEquals(0, stockService.consume(null, list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        List<Stock> list = new ArrayList<>();
        assertEquals(2, stockService.consume(null, list::add, emptySet()));
        assertEquals(2, list.size());
        assertEquals(stock1, list.get(0));
        assertEquals(stock2, list.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        List<Stock> list = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        assertEquals(2, stockService.consume(null, list::add, sort));
        assertEquals(2, list.size());
        assertEquals(stock2, list.get(0));
        assertEquals(stock1, list.get(1));
    }

    @Test
    public void testConsumeMixedActive() {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(YOUTUBE).setSymbol("sym3").setName("name3").setActive(false);
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));

        List<Stock> nullList = new ArrayList<>();
        assertEquals(3, stockService.consume(null, nullList::add, emptySet()));
        assertEquals(3, nullList.size());

        List<Stock> trueList = new ArrayList<>();
        assertEquals(2, stockService.consume(true, trueList::add, emptySet()));
        assertEquals(2, trueList.size());

        List<Stock> falseList = new ArrayList<>();
        assertEquals(1, stockService.consume(false, falseList::add, emptySet()));
        assertEquals(1, falseList.size());
    }

    @Test
    public void testAdd() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
    }

    @Test
    public void testAddConflictSameName() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
        assertEquals(0, stockService.add(stock));

        Optional<Stock> fetched = stockService.get(TWITTER, stock.getSymbol(), null);
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
    }

    @Test
    public void testAddConflictDifferentName() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
        stock.setName("updated");
        assertEquals(1, stockService.add(stock));

        Optional<Stock> fetched = stockService.get(TWITTER, stock.getSymbol(), null);
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
    }

    @Test
    public void testUpdateMissing() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(0, stockService.update(stock));
    }

    @Test
    public void testUpdate() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));

        stock.setName("updated");
        assertEquals(1, stockService.update(stock));

        Optional<Stock> updated = stockService.get(TWITTER, stock.getSymbol(), null);
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
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
        assertEquals(1, stockService.delete(TWITTER, stock.getSymbol()));
        assertFalse(stockService.get(TWITTER, stock.getSymbol(), null).isPresent());
    }
}
