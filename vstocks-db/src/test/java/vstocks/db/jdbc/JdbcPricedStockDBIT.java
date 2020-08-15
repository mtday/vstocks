package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.StockPriceTable;
import vstocks.db.jdbc.table.StockTable;

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
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.Sort.SortDirection.DESC;

public class JdbcPricedStockDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private StockTable stockTable;
    private StockPriceTable stockPriceTable;

    private JdbcStockDB stockDB;
    private JdbcStockPriceDB stockPriceDB;
    private JdbcPricedStockDB pricedStockDB;

    @Before
    public void setup() throws SQLException {
        stockTable = new StockTable();
        stockPriceTable = new StockPriceTable();
        stockDB = new JdbcStockDB(dataSourceExternalResource.get());
        stockPriceDB = new JdbcStockPriceDB(dataSourceExternalResource.get());
        pricedStockDB = new JdbcPricedStockDB(dataSourceExternalResource.get());

        // Clear out the initial stocks from the Flyway script
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            stockTable.truncate(connection);
            connection.commit();
        }
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
    public void testGetMissing() {
        assertFalse(pricedStockDB.get(TWITTER, "missing").isPresent());
    }

    @Test
    public void testGetExistsNoPrice() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        assertEquals(1, stockDB.add(stock));

        Optional<PricedStock> fetched = pricedStockDB.get(TWITTER, stock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertNotNull(fetched.get().getTimestamp()); // defaults to Instant.now()
        assertEquals(1, fetched.get().getPrice()); // defaults to 1
    }

    @Test
    public void testGetExistsWithNoPrice() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        assertEquals(1, stockDB.add(stock));

        Optional<PricedStock> fetched = pricedStockDB.get(TWITTER, stock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertNotNull(fetched.get().getTimestamp()); // defaults to Instant.now()
        assertEquals(1, fetched.get().getPrice()); // defaults to 1
    }

    @Test
    public void testGetExistsWithSinglePrice() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol(stock.getSymbol()).setTimestamp(now).setPrice(10);
        assertEquals(1, stockDB.add(stock));
        assertEquals(1, stockPriceDB.add(stockPrice));

        Optional<PricedStock> fetched = pricedStockDB.get(TWITTER, stock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertEquals(stockPrice.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(stockPrice.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testGetExistsWithMultiplePrices() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        assertEquals(1, stockDB.add(stock));
        assertEquals(1, stockPriceDB.add(stockPrice1));
        assertEquals(1, stockPriceDB.add(stockPrice2));

        Optional<PricedStock> fetched = pricedStockDB.get(TWITTER, stock.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertEquals(stockPrice1.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(stockPrice1.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testGetForMarketNone() {
        Results<PricedStock> results = pricedStockDB.getForMarket(TWITTER, new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForMarketSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setImageLink("link");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));
        assertEquals(1, stockPriceDB.add(stock1Price1));
        assertEquals(1, stockPriceDB.add(stock1Price2));
        assertEquals(1, stockPriceDB.add(stock2Price1));
        assertEquals(1, stockPriceDB.add(stock2Price2));

        Results<PricedStock> results = pricedStockDB.getForMarket(TWITTER, new Page(), emptySet());
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

    @Test
    public void testGetForMarketSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setImageLink("link");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));
        assertEquals(1, stockPriceDB.add(stock1Price1));
        assertEquals(1, stockPriceDB.add(stock1Price2));
        assertEquals(1, stockPriceDB.add(stock2Price1));
        assertEquals(1, stockPriceDB.add(stock2Price2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        Results<PricedStock> results = pricedStockDB.getForMarket(TWITTER, new Page(), sort);
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

    @Test
    public void testConsumeForMarketNone() {
        List<PricedStock> results = new ArrayList<>();
        assertEquals(0, pricedStockDB.consumeForMarket(TWITTER, results::add, emptySet()));
        assertTrue(results.isEmpty());
    }

    @Test
    public void testConsumeForMarketSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setImageLink("link");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));
        assertEquals(1, stockPriceDB.add(stock1Price1));
        assertEquals(1, stockPriceDB.add(stock1Price2));
        assertEquals(1, stockPriceDB.add(stock2Price1));
        assertEquals(1, stockPriceDB.add(stock2Price2));

        List<PricedStock> results = new ArrayList<>();
        assertEquals(2, pricedStockDB.consumeForMarket(TWITTER, results::add, emptySet()));
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

    @Test
    public void testConsumeForMarketSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2").setImageLink("link");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));
        assertEquals(1, stockPriceDB.add(stock1Price1));
        assertEquals(1, stockPriceDB.add(stock1Price2));
        assertEquals(1, stockPriceDB.add(stock2Price1));
        assertEquals(1, stockPriceDB.add(stock2Price2));

        List<PricedStock> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), PRICE.toSort()));
        assertEquals(2, pricedStockDB.consumeForMarket(TWITTER, results::add, sort));
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

    @Test
    public void testGetAllNone() {
        Results<PricedStock> results = pricedStockDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setImageLink("link");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));
        assertEquals(1, stockPriceDB.add(stock1Price1));
        assertEquals(1, stockPriceDB.add(stock1Price2));
        assertEquals(1, stockPriceDB.add(stock2Price1));
        assertEquals(1, stockPriceDB.add(stock2Price2));

        Results<PricedStock> results = pricedStockDB.getAll(new Page(), emptySet());
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

    @Test
    public void testGetAllSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setImageLink("link");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));
        assertEquals(1, stockPriceDB.add(stock1Price1));
        assertEquals(1, stockPriceDB.add(stock1Price2));
        assertEquals(1, stockPriceDB.add(stock2Price1));
        assertEquals(1, stockPriceDB.add(stock2Price2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), PRICE.toSort()));
        Results<PricedStock> results = pricedStockDB.getAll(new Page(), sort);
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

    @Test
    public void testConsumeNone() {
        List<PricedStock> list = new ArrayList<>();
        assertEquals(0, pricedStockDB.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setImageLink("link");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));
        assertEquals(1, stockPriceDB.add(stock1Price1));
        assertEquals(1, stockPriceDB.add(stock1Price2));
        assertEquals(1, stockPriceDB.add(stock2Price1));
        assertEquals(1, stockPriceDB.add(stock2Price2));

        List<PricedStock> results = new ArrayList<>();
        assertEquals(2, pricedStockDB.consume(results::add, emptySet()));
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

    @Test
    public void testConsumeSomeWithSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setImageLink("link");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setImageLink("link");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockDB.add(stock1));
        assertEquals(1, stockDB.add(stock2));
        assertEquals(1, stockPriceDB.add(stock1Price1));
        assertEquals(1, stockPriceDB.add(stock1Price2));
        assertEquals(1, stockPriceDB.add(stock2Price1));
        assertEquals(1, stockPriceDB.add(stock2Price2));

        List<PricedStock> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), PRICE.toSort()));
        assertEquals(2, pricedStockDB.consume(results::add, sort));
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

    @Test
    public void testAdd() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setImageLink("link").setTimestamp(now).setPrice(10);

        assertEquals(1, pricedStockDB.add(pricedStock));

        Optional<Stock> stock = stockDB.get(pricedStock.getMarket(), pricedStock.getSymbol());
        Optional<StockPrice> stockPrice = stockPriceDB.getLatest(pricedStock.getMarket(), pricedStock.getSymbol());

        assertTrue(stock.isPresent());
        assertTrue(stockPrice.isPresent());

        assertEquals(pricedStock.getMarket(), stock.get().getMarket());
        assertEquals(pricedStock.getSymbol(), stock.get().getSymbol());
        assertEquals(pricedStock.getName(), stock.get().getName());
        assertEquals(pricedStock.getImageLink(), stock.get().getImageLink());
        assertEquals(pricedStock.getMarket(), stockPrice.get().getMarket());
        assertEquals(pricedStock.getSymbol(), stockPrice.get().getSymbol());
        assertEquals(pricedStock.getTimestamp(), stockPrice.get().getTimestamp());
        assertEquals(pricedStock.getPrice(), stockPrice.get().getPrice());
    }

    @Test
    public void testAddNoImageLink() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setTimestamp(now).setPrice(10);

        assertEquals(1, pricedStockDB.add(pricedStock));

        Optional<Stock> stock = stockDB.get(pricedStock.getMarket(), pricedStock.getSymbol());
        Optional<StockPrice> stockPrice = stockPriceDB.getLatest(pricedStock.getMarket(), pricedStock.getSymbol());

        assertTrue(stock.isPresent());
        assertTrue(stockPrice.isPresent());

        assertEquals(pricedStock.getMarket(), stock.get().getMarket());
        assertEquals(pricedStock.getSymbol(), stock.get().getSymbol());
        assertEquals(pricedStock.getName(), stock.get().getName());
        assertNull(stock.get().getImageLink());
        assertEquals(pricedStock.getMarket(), stockPrice.get().getMarket());
        assertEquals(pricedStock.getSymbol(), stockPrice.get().getSymbol());
        assertEquals(pricedStock.getTimestamp(), stockPrice.get().getTimestamp());
        assertEquals(pricedStock.getPrice(), stockPrice.get().getPrice());
    }

    @Test
    public void testAddAlreadyExists() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setTimestamp(now).setPrice(10);

        assertEquals(1, pricedStockDB.add(pricedStock));
        assertEquals(0, pricedStockDB.add(pricedStock));
    }
}
