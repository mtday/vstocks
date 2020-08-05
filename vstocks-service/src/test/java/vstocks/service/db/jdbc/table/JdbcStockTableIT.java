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
import static vstocks.model.Market.YOUTUBE;
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
            assertFalse(stockTable.get(connection, TWITTER, "missing", null).isPresent());
        }
    }

    @Test
    public void testGetExistsActive() throws SQLException {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> nullFetched = stockTable.get(connection, TWITTER, stock.getSymbol(), null);
            assertTrue(nullFetched.isPresent());
            assertEquals(stock, nullFetched.get()); // only compares id

            Optional<Stock> trueFetched = stockTable.get(connection, TWITTER, stock.getSymbol(), true);
            assertTrue(trueFetched.isPresent());
            assertEquals(stock, trueFetched.get()); // only compares id

            assertFalse(stockTable.get(connection, TWITTER, stock.getSymbol(), false).isPresent());
        }
    }

    @Test
    public void testGetExistsInactive() throws SQLException {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("name").setActive(false);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> nullFetched = stockTable.get(connection, TWITTER, stock.getSymbol(), null);
            assertTrue(nullFetched.isPresent());
            assertEquals(stock, nullFetched.get()); // only compares id

            assertFalse(stockTable.get(connection, TWITTER, stock.getSymbol(), true).isPresent());

            Optional<Stock> falseFetched = stockTable.get(connection, TWITTER, stock.getSymbol(), false);
            assertTrue(falseFetched.isPresent());
            assertEquals(stock, falseFetched.get()); // only compares id
        }
    }

    @Test
    public void testGetForMarket() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getForMarket(connection, TWITTER, null, new Page(), emptySet());
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
            Results<Stock> results = stockTable.getForMarket(connection, TWITTER, null, new Page(), emptySet());
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
            Results<Stock> results = stockTable.getForMarket(connection, TWITTER, null, new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(stock2, results.getResults().get(0));
            assertEquals(stock1, results.getResults().get(1));
        }
    }

    @Test
    public void testGetForMarketMixedActive() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(TWITTER).setSymbol("sym3").setName("name3").setActive(false);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockTable.add(connection, stock3));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> nullResults = stockTable.getForMarket(connection, TWITTER, null, new Page(), emptySet());
            assertEquals(3, nullResults.getTotal());
            assertEquals(3, nullResults.getResults().size());

            Results<Stock> trueResults = stockTable.getForMarket(connection, TWITTER, true, new Page(), emptySet());
            assertEquals(2, trueResults.getTotal());
            assertEquals(2, trueResults.getResults().size());

            Results<Stock> falseResults = stockTable.getForMarket(connection, TWITTER, false, new Page(), emptySet());
            assertEquals(1, falseResults.getTotal());
            assertEquals(1, falseResults.getResults().size());
        }
    }

    @Test
    public void testConsumeForMarket() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> results = new ArrayList<>();
            assertEquals(0, stockTable.consumeForMarket(connection, TWITTER, null, results::add, emptySet()));
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
            assertEquals(2, stockTable.consumeForMarket(connection, TWITTER, null, results::add, emptySet()));
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
            assertEquals(2, stockTable.consumeForMarket(connection, TWITTER, null, results::add, sort));
            assertEquals(2, results.size());
            assertEquals(stock2, results.get(0));
            assertEquals(stock1, results.get(1));
        }
    }

    @Test
    public void testConsumeForMarketMixedActive() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(TWITTER).setSymbol("sym3").setName("name3").setActive(false);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockTable.add(connection, stock3));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> nullResults = new ArrayList<>();
            assertEquals(3, stockTable.consumeForMarket(connection, TWITTER, null, nullResults::add, emptySet()));
            assertEquals(3, nullResults.size());

            List<Stock> trueResults = new ArrayList<>();
            assertEquals(2, stockTable.consumeForMarket(connection, TWITTER, true, trueResults::add, emptySet()));
            assertEquals(2, trueResults.size());

            List<Stock> falseResults = new ArrayList<>();
            assertEquals(1, stockTable.consumeForMarket(connection, TWITTER, false, falseResults::add, emptySet()));
            assertEquals(1, falseResults.size());
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getAll(connection, null, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSomeNoSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> results = stockTable.getAll(connection, null, new Page(), emptySet());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(stock1, results.getResults().get(0));
            assertEquals(stock2, results.getResults().get(1));
        }
    }

    @Test
    public void testGetAllSomeWithSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
            Results<Stock> results = stockTable.getAll(connection, null, new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertEquals(stock2, results.getResults().get(0));
            assertEquals(stock1, results.getResults().get(1));
        }
    }

    @Test
    public void testGetAllMixedActive() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(YOUTUBE).setSymbol("sym3").setName("name3").setActive(false);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockTable.add(connection, stock3));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<Stock> nullResults = stockTable.getAll(connection, null, new Page(), emptySet());
            assertEquals(3, nullResults.getTotal());
            assertEquals(3, nullResults.getResults().size());

            Results<Stock> trueResults = stockTable.getAll(connection, true, new Page(), emptySet());
            assertEquals(2, trueResults.getTotal());
            assertEquals(2, trueResults.getResults().size());

            Results<Stock> falseResults = stockTable.getAll(connection, false, new Page(), emptySet());
            assertEquals(1, falseResults.getTotal());
            assertEquals(1, falseResults.getResults().size());
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> list = new ArrayList<>();
            assertEquals(0, stockTable.consume(connection, null, list::add, emptySet()));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSomeNoSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> list = new ArrayList<>();
            assertEquals(2, stockTable.consume(connection, null, list::add, emptySet()));
            assertEquals(2, list.size());
            assertEquals(stock1, list.get(0));
            assertEquals(stock2, list.get(1));
        }
    }

    @Test
    public void testConsumeSomeWithSort() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> list = new ArrayList<>();
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
            assertEquals(2, stockTable.consume(connection, null, list::add, sort));
            assertEquals(2, list.size());
            assertEquals(stock2, list.get(0));
            assertEquals(stock1, list.get(1));
        }
    }

    @Test
    public void testConsumeMixedActive() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(YOUTUBE).setSymbol("sym3").setName("name3").setActive(false);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockTable.add(connection, stock3));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<Stock> nullList = new ArrayList<>();
            assertEquals(3, stockTable.consume(connection, null, nullList::add, emptySet()));
            assertEquals(3, nullList.size());

            List<Stock> trueList = new ArrayList<>();
            assertEquals(2, stockTable.consume(connection, true, trueList::add, emptySet()));
            assertEquals(2, trueList.size());

            List<Stock> falseList = new ArrayList<>();
            assertEquals(1, stockTable.consume(connection, false, falseList::add, emptySet()));
            assertEquals(1, falseList.size());
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
            Optional<Stock> updated = stockTable.get(connection, TWITTER, stock.getSymbol(), null);
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
            Optional<Stock> updated = stockTable.get(connection, TWITTER, stock.getSymbol(), null);
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
            assertFalse(stockTable.get(connection, TWITTER, stock.getSymbol(), null).isPresent());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
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
            Results<Stock> results = stockTable.getAll(connection, null, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
