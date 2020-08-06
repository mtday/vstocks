package vstocks.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.db.DataSourceExternalResource;

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

public class JdbcPricedStockJoinIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private StockTable stockTable;
    private StockPriceTable stockPriceTable;
    private PricedStockJoin pricedStockJoin;

    @Before
    public void setup() {
        stockTable = new StockTable();
        stockPriceTable = new StockPriceTable();
        pricedStockJoin = new PricedStockJoin();
    }

    @After
    public void cleanup() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockPriceTable.truncate(connection);
            stockTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(pricedStockJoin.get(connection, TWITTER, "missing", null).isPresent());
        }
    }

    @Test
    public void testGetExistsNoPrice() throws SQLException {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("name");
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<PricedStock> fetched = pricedStockJoin.get(connection, TWITTER, stock.getSymbol(), null);
            assertTrue(fetched.isPresent());
            assertEquals(stock.getMarket(), fetched.get().getMarket());
            assertEquals(stock.getSymbol(), fetched.get().getSymbol());
            assertEquals(stock.getName(), fetched.get().getName());
            assertNotNull(fetched.get().getTimestamp());
            assertEquals(1, fetched.get().getPrice()); // defaults to price of 1
        }
    }

    @Test
    public void testGetExistsWithSinglePrice() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("name");
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol("symbol").setTimestamp(now).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<PricedStock> fetched = pricedStockJoin.get(connection, TWITTER, stock.getSymbol(), null);
            assertTrue(fetched.isPresent());
            assertEquals(stock.getMarket(), fetched.get().getMarket());
            assertEquals(stock.getSymbol(), fetched.get().getSymbol());
            assertEquals(stock.getName(), fetched.get().getName());
            assertEquals(stockPrice1.getTimestamp(), fetched.get().getTimestamp());
            assertEquals(stockPrice1.getPrice(), fetched.get().getPrice());
        }
    }

    @Test
    public void testGetExistsWithMultiplePrices() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("symbol").setName("name");
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol("symbol").setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol("symbol").setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock));
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<PricedStock> fetched = pricedStockJoin.get(connection, TWITTER, stock.getSymbol(), null);
            assertTrue(fetched.isPresent());
            assertEquals(stock.getMarket(), fetched.get().getMarket());
            assertEquals(stock.getSymbol(), fetched.get().getSymbol());
            assertEquals(stock.getName(), fetched.get().getName());
            assertEquals(stockPrice1.getTimestamp(), fetched.get().getTimestamp());
            assertEquals(stockPrice1.getPrice(), fetched.get().getPrice());
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
            Optional<PricedStock> nullFetched = pricedStockJoin.get(connection, TWITTER, stock.getSymbol(), null);
            assertTrue(nullFetched.isPresent());
            assertEquals(stock.getMarket(), nullFetched.get().getMarket());
            assertEquals(stock.getSymbol(), nullFetched.get().getSymbol());
            assertEquals(stock.getName(), nullFetched.get().getName());
            assertEquals(stock.isActive(), nullFetched.get().isActive());

            assertFalse(pricedStockJoin.get(connection, TWITTER, stock.getSymbol(), true).isPresent());

            Optional<PricedStock> falseFetched = pricedStockJoin.get(connection, TWITTER, stock.getSymbol(), false);
            assertTrue(falseFetched.isPresent());
            assertEquals(stock.getMarket(), falseFetched.get().getMarket());
            assertEquals(stock.getSymbol(), falseFetched.get().getSymbol());
            assertEquals(stock.getName(), falseFetched.get().getName());
            assertEquals(stock.isActive(), falseFetched.get().isActive());
        }
    }

    @Test
    public void testGetForMarket() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<PricedStock> results = pricedStockJoin.getForMarket(connection, TWITTER, null, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForMarketSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<PricedStock> results = pricedStockJoin.getForMarket(connection, TWITTER, null, new Page(), emptySet());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());

            PricedStock pricedStock1 = results.getResults().get(0);
            assertEquals(stock1.getMarket(), pricedStock1.getMarket());
            assertEquals(stock1.getSymbol(), pricedStock1.getSymbol());
            assertEquals(stock1.getName(), pricedStock1.getName());
            assertEquals(stock1Price1.getTimestamp(), pricedStock1.getTimestamp());
            assertEquals(stock1Price1.getPrice(), pricedStock1.getPrice());

            PricedStock pricedStock2 = results.getResults().get(1);
            assertEquals(stock2.getMarket(), pricedStock2.getMarket());
            assertEquals(stock2.getSymbol(), pricedStock2.getSymbol());
            assertEquals(stock2.getName(), pricedStock2.getName());
            assertEquals(stock2Price1.getTimestamp(), pricedStock2.getTimestamp());
            assertEquals(stock2Price1.getPrice(), pricedStock2.getPrice());
        }
    }

    @Test
    public void testGetForMarketSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
            Results<PricedStock> results = pricedStockJoin.getForMarket(connection, TWITTER, null, new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());

            PricedStock pricedStock1 = results.getResults().get(0);
            assertEquals(stock2.getMarket(), pricedStock1.getMarket());
            assertEquals(stock2.getSymbol(), pricedStock1.getSymbol());
            assertEquals(stock2.getName(), pricedStock1.getName());
            assertEquals(stock2Price1.getTimestamp(), pricedStock1.getTimestamp());
            assertEquals(stock2Price1.getPrice(), pricedStock1.getPrice());

            PricedStock pricedStock2 = results.getResults().get(1);
            assertEquals(stock1.getMarket(), pricedStock2.getMarket());
            assertEquals(stock1.getSymbol(), pricedStock2.getSymbol());
            assertEquals(stock1.getName(), pricedStock2.getName());
            assertEquals(stock1Price1.getTimestamp(), pricedStock2.getTimestamp());
            assertEquals(stock1Price1.getPrice(), pricedStock2.getPrice());
        }
    }

    @Test
    public void testGetForMarketMixedActive() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(TWITTER).setSymbol("sym3").setName("name3").setActive(false);
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock3Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock3.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock3Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock3.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockTable.add(connection, stock3));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            assertEquals(1, stockPriceTable.add(connection, stock3Price1));
            assertEquals(1, stockPriceTable.add(connection, stock3Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<PricedStock> nullResults = pricedStockJoin.getForMarket(connection, TWITTER, null, new Page(), emptySet());
            assertEquals(3, nullResults.getTotal());
            assertEquals(3, nullResults.getResults().size());

            Results<PricedStock> trueResults = pricedStockJoin.getForMarket(connection, TWITTER, true, new Page(), emptySet());
            assertEquals(2, trueResults.getTotal());
            assertEquals(2, trueResults.getResults().size());

            Results<PricedStock> falseResults = pricedStockJoin.getForMarket(connection, TWITTER, false, new Page(), emptySet());
            assertEquals(1, falseResults.getTotal());
            assertEquals(1, falseResults.getResults().size());
        }
    }

    @Test
    public void testConsumeForMarket() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<PricedStock> results = new ArrayList<>();
            assertEquals(0, pricedStockJoin.consumeForMarket(connection, TWITTER, null, results::add, emptySet()));
            assertTrue(results.isEmpty());
        }
    }

    @Test
    public void testConsumeForMarketSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<PricedStock> results = new ArrayList<>();
            assertEquals(2, pricedStockJoin.consumeForMarket(connection, TWITTER, null, results::add, emptySet()));
            assertEquals(2, results.size());

            PricedStock pricedStock1 = results.get(0);
            assertEquals(stock1.getMarket(), pricedStock1.getMarket());
            assertEquals(stock1.getSymbol(), pricedStock1.getSymbol());
            assertEquals(stock1.getName(), pricedStock1.getName());
            assertEquals(stock1Price1.getTimestamp(), pricedStock1.getTimestamp());
            assertEquals(stock1Price1.getPrice(), pricedStock1.getPrice());

            PricedStock pricedStock2 = results.get(1);
            assertEquals(stock2.getMarket(), pricedStock2.getMarket());
            assertEquals(stock2.getSymbol(), pricedStock2.getSymbol());
            assertEquals(stock2.getName(), pricedStock2.getName());
            assertEquals(stock2Price1.getTimestamp(), pricedStock2.getTimestamp());
            assertEquals(stock2Price1.getPrice(), pricedStock2.getPrice());
        }
    }

    @Test
    public void testConsumeForMarketSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<PricedStock> results = new ArrayList<>();
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
            assertEquals(2, pricedStockJoin.consumeForMarket(connection, TWITTER, null, results::add, sort));
            assertEquals(2, results.size());

            PricedStock pricedStock1 = results.get(0);
            assertEquals(stock2.getMarket(), pricedStock1.getMarket());
            assertEquals(stock2.getSymbol(), pricedStock1.getSymbol());
            assertEquals(stock2.getName(), pricedStock1.getName());
            assertEquals(stock2Price1.getTimestamp(), pricedStock1.getTimestamp());
            assertEquals(stock2Price1.getPrice(), pricedStock1.getPrice());

            PricedStock pricedStock2 = results.get(1);
            assertEquals(stock1.getMarket(), pricedStock2.getMarket());
            assertEquals(stock1.getSymbol(), pricedStock2.getSymbol());
            assertEquals(stock1.getName(), pricedStock2.getName());
            assertEquals(stock1Price1.getTimestamp(), pricedStock2.getTimestamp());
            assertEquals(stock1Price1.getPrice(), pricedStock2.getPrice());
        }
    }

    @Test
    public void testConsumeForMarketMixedActive() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(TWITTER).setSymbol("sym3").setName("name3").setActive(false);
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock3Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock3.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock3Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock3.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockTable.add(connection, stock3));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            assertEquals(1, stockPriceTable.add(connection, stock3Price1));
            assertEquals(1, stockPriceTable.add(connection, stock3Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<PricedStock> nullResults = new ArrayList<>();
            assertEquals(3, pricedStockJoin.consumeForMarket(connection, TWITTER, null, nullResults::add, emptySet()));
            assertEquals(3, nullResults.size());

            List<PricedStock> trueResults = new ArrayList<>();
            assertEquals(2, pricedStockJoin.consumeForMarket(connection, TWITTER, true, trueResults::add, emptySet()));
            assertEquals(2, trueResults.size());

            List<PricedStock> falseResults = new ArrayList<>();
            assertEquals(1, pricedStockJoin.consumeForMarket(connection, TWITTER, false, falseResults::add, emptySet()));
            assertEquals(1, falseResults.size());
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<PricedStock> results = pricedStockJoin.getAll(connection, null, new Page(), emptySet());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<PricedStock> results = pricedStockJoin.getAll(connection, null, new Page(), emptySet());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());

            PricedStock pricedStock1 = results.getResults().get(0);
            assertEquals(stock1.getMarket(), pricedStock1.getMarket());
            assertEquals(stock1.getSymbol(), pricedStock1.getSymbol());
            assertEquals(stock1.getName(), pricedStock1.getName());
            assertEquals(stock1Price1.getTimestamp(), pricedStock1.getTimestamp());
            assertEquals(stock1Price1.getPrice(), pricedStock1.getPrice());

            PricedStock pricedStock2 = results.getResults().get(1);
            assertEquals(stock2.getMarket(), pricedStock2.getMarket());
            assertEquals(stock2.getSymbol(), pricedStock2.getSymbol());
            assertEquals(stock2.getName(), pricedStock2.getName());
            assertEquals(stock2Price1.getTimestamp(), pricedStock2.getTimestamp());
            assertEquals(stock2Price1.getPrice(), pricedStock2.getPrice());
        }
    }

    @Test
    public void testGetAllSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
            Results<PricedStock> results = pricedStockJoin.getAll(connection, null, new Page(), sort);
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());

            PricedStock pricedStock1 = results.getResults().get(0);
            assertEquals(stock2.getMarket(), pricedStock1.getMarket());
            assertEquals(stock2.getSymbol(), pricedStock1.getSymbol());
            assertEquals(stock2.getName(), pricedStock1.getName());
            assertEquals(stock2Price1.getTimestamp(), pricedStock1.getTimestamp());
            assertEquals(stock2Price1.getPrice(), pricedStock1.getPrice());

            PricedStock pricedStock2 = results.getResults().get(1);
            assertEquals(stock1.getMarket(), pricedStock2.getMarket());
            assertEquals(stock1.getSymbol(), pricedStock2.getSymbol());
            assertEquals(stock1.getName(), pricedStock2.getName());
            assertEquals(stock1Price1.getTimestamp(), pricedStock2.getTimestamp());
            assertEquals(stock1Price1.getPrice(), pricedStock2.getPrice());
        }
    }

    @Test
    public void testGetAllMixedActive() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(TWITTER).setSymbol("sym3").setName("name3").setActive(false);
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock3Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock3.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock3Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock3.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockTable.add(connection, stock3));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            assertEquals(1, stockPriceTable.add(connection, stock3Price1));
            assertEquals(1, stockPriceTable.add(connection, stock3Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<PricedStock> nullResults = pricedStockJoin.getAll(connection, null, new Page(), emptySet());
            assertEquals(3, nullResults.getTotal());
            assertEquals(3, nullResults.getResults().size());

            Results<PricedStock> trueResults = pricedStockJoin.getAll(connection, true, new Page(), emptySet());
            assertEquals(2, trueResults.getTotal());
            assertEquals(2, trueResults.getResults().size());

            Results<PricedStock> falseResults = pricedStockJoin.getAll(connection, false, new Page(), emptySet());
            assertEquals(1, falseResults.getTotal());
            assertEquals(1, falseResults.getResults().size());
        }
    }

    @Test
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<PricedStock> list = new ArrayList<>();
            assertEquals(0, pricedStockJoin.consume(connection, null, list::add, emptySet()));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSomeNoSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<PricedStock> results = new ArrayList<>();
            assertEquals(2, pricedStockJoin.consume(connection, null, results::add, emptySet()));
            assertEquals(2, results.size());

            PricedStock pricedStock1 = results.get(0);
            assertEquals(stock1.getMarket(), pricedStock1.getMarket());
            assertEquals(stock1.getSymbol(), pricedStock1.getSymbol());
            assertEquals(stock1.getName(), pricedStock1.getName());
            assertEquals(stock1Price1.getTimestamp(), pricedStock1.getTimestamp());
            assertEquals(stock1Price1.getPrice(), pricedStock1.getPrice());

            PricedStock pricedStock2 = results.get(1);
            assertEquals(stock2.getMarket(), pricedStock2.getMarket());
            assertEquals(stock2.getSymbol(), pricedStock2.getSymbol());
            assertEquals(stock2.getName(), pricedStock2.getName());
            assertEquals(stock2Price1.getTimestamp(), pricedStock2.getTimestamp());
            assertEquals(stock2Price1.getPrice(), pricedStock2.getPrice());
        }
    }

    @Test
    public void testConsumeSomeWithSort() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<PricedStock> results = new ArrayList<>();
            Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
            assertEquals(2, pricedStockJoin.consume(connection, null, results::add, sort));
            assertEquals(2, results.size());

            PricedStock pricedStock1 = results.get(0);
            assertEquals(stock2.getMarket(), pricedStock1.getMarket());
            assertEquals(stock2.getSymbol(), pricedStock1.getSymbol());
            assertEquals(stock2.getName(), pricedStock1.getName());
            assertEquals(stock2Price1.getTimestamp(), pricedStock1.getTimestamp());
            assertEquals(stock2Price1.getPrice(), pricedStock1.getPrice());

            PricedStock pricedStock2 = results.get(1);
            assertEquals(stock1.getMarket(), pricedStock2.getMarket());
            assertEquals(stock1.getSymbol(), pricedStock2.getSymbol());
            assertEquals(stock1.getName(), pricedStock2.getName());
            assertEquals(stock1Price1.getTimestamp(), pricedStock2.getTimestamp());
            assertEquals(stock1Price1.getPrice(), pricedStock2.getPrice());
        }
    }

    @Test
    public void testConsumeMixedActive() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(TWITTER).setSymbol("sym3").setName("name3").setActive(false);
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock3Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock3.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock3Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock3.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
            assertEquals(1, stockTable.add(connection, stock3));
            assertEquals(1, stockPriceTable.add(connection, stock1Price1));
            assertEquals(1, stockPriceTable.add(connection, stock1Price2));
            assertEquals(1, stockPriceTable.add(connection, stock2Price1));
            assertEquals(1, stockPriceTable.add(connection, stock2Price2));
            assertEquals(1, stockPriceTable.add(connection, stock3Price1));
            assertEquals(1, stockPriceTable.add(connection, stock3Price2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<PricedStock> nullResults = new ArrayList<>();
            assertEquals(3, pricedStockJoin.consume(connection, null, nullResults::add, emptySet()));
            assertEquals(3, nullResults.size());

            List<PricedStock> trueResults = new ArrayList<>();
            assertEquals(2, pricedStockJoin.consume(connection, true, trueResults::add, emptySet()));
            assertEquals(2, trueResults.size());

            List<PricedStock> falseResults = new ArrayList<>();
            assertEquals(1, pricedStockJoin.consume(connection, false, falseResults::add, emptySet()));
            assertEquals(1, falseResults.size());
        }
    }

    @Test
    public void testAdd() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setTimestamp(now).setPrice(10);

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, pricedStockJoin.add(connection, pricedStock));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> stock = stockTable.get(connection, pricedStock.getMarket(), pricedStock.getSymbol(), null);
            Optional<StockPrice> stockPrice = stockPriceTable.getLatest(connection, pricedStock.getMarket(), pricedStock.getSymbol());

            assertTrue(stock.isPresent());
            assertTrue(stockPrice.isPresent());

            assertEquals(pricedStock.getMarket(), stock.get().getMarket());
            assertEquals(pricedStock.getSymbol(), stock.get().getSymbol());
            assertEquals(pricedStock.getName(), stock.get().getName());
            assertEquals(pricedStock.isActive(), stock.get().isActive());
            assertEquals(pricedStock.getMarket(), stockPrice.get().getMarket());
            assertEquals(pricedStock.getSymbol(), stockPrice.get().getSymbol());
            assertEquals(pricedStock.getTimestamp(), stockPrice.get().getTimestamp());
            assertEquals(pricedStock.getPrice(), stockPrice.get().getPrice());
        }
    }

    @Test
    public void testAddInactive() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setTimestamp(now).setPrice(10).setActive(false);

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, pricedStockJoin.add(connection, pricedStock));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<Stock> stock = stockTable.get(connection, pricedStock.getMarket(), pricedStock.getSymbol(), null);
            Optional<StockPrice> stockPrice = stockPriceTable.getLatest(connection, pricedStock.getMarket(), pricedStock.getSymbol());

            assertTrue(stock.isPresent());
            assertTrue(stockPrice.isPresent());

            assertEquals(pricedStock.getMarket(), stock.get().getMarket());
            assertEquals(pricedStock.getSymbol(), stock.get().getSymbol());
            assertEquals(pricedStock.getName(), stock.get().getName());
            assertEquals(pricedStock.isActive(), stock.get().isActive());
            assertEquals(pricedStock.getMarket(), stockPrice.get().getMarket());
            assertEquals(pricedStock.getSymbol(), stockPrice.get().getSymbol());
            assertEquals(pricedStock.getTimestamp(), stockPrice.get().getTimestamp());
            assertEquals(pricedStock.getPrice(), stockPrice.get().getPrice());
        }
    }

    @Test
    public void testAddAlreadyExists() throws SQLException {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setTimestamp(now).setPrice(10);

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, pricedStockJoin.add(connection, pricedStock));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, pricedStockJoin.add(connection, pricedStock));
            connection.commit();
        }
    }
}
