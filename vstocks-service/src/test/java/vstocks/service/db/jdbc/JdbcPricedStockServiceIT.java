package vstocks.service.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.service.db.DataSourceExternalResource;
import vstocks.service.db.jdbc.table.StockPriceTable;
import vstocks.service.db.jdbc.table.StockTable;

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

public class JdbcPricedStockServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private StockTable stockTable;
    private StockPriceTable stockPriceTable;

    private JdbcStockService stockService;
    private JdbcStockPriceService stockPriceService;
    private JdbcPricedStockService pricedStockService;

    @Before
    public void setup() {
        stockTable = new StockTable();
        stockPriceTable = new StockPriceTable();
        stockService = new JdbcStockService(dataSourceExternalResource.get());
        stockPriceService = new JdbcStockPriceService(dataSourceExternalResource.get());
        pricedStockService = new JdbcPricedStockService(dataSourceExternalResource.get());
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
        assertFalse(pricedStockService.get(TWITTER, "missing", null).isPresent());
    }

    @Test
    public void testGetExistsInactiveNullActiveNoPrice() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(false);
        assertEquals(1, stockService.add(stock));

        Optional<PricedStock> fetched = pricedStockService.get(TWITTER, stock.getSymbol(), null);
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertNotNull(fetched.get().getTimestamp()); // defaults to Instant.now()
        assertEquals(1, fetched.get().getPrice()); // defaults to 1
    }

    @Test
    public void testGetExistsInactiveTrueActiveNoPrice() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(false);
        assertEquals(1, stockService.add(stock));

        assertFalse(pricedStockService.get(TWITTER, stock.getSymbol(), true).isPresent());
    }

    @Test
    public void testGetExistsInactiveFalseActiveNoPrice() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(false);
        assertEquals(1, stockService.add(stock));

        Optional<PricedStock> fetched = pricedStockService.get(TWITTER, stock.getSymbol(), false);
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertNotNull(fetched.get().getTimestamp()); // defaults to Instant.now()
        assertEquals(1, fetched.get().getPrice()); // defaults to 1
    }

    @Test
    public void testGetExistsWithNoPrice() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        assertEquals(1, stockService.add(stock));

        Optional<PricedStock> fetched = pricedStockService.get(TWITTER, stock.getSymbol(), null);
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
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol(stock.getSymbol()).setTimestamp(now).setPrice(10);
        assertEquals(1, stockService.add(stock));
        assertEquals(1, stockPriceService.add(stockPrice));

        Optional<PricedStock> fetched = pricedStockService.get(TWITTER, stock.getSymbol(), null);
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
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        assertEquals(1, stockService.add(stock));
        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));

        Optional<PricedStock> fetched = pricedStockService.get(TWITTER, stock.getSymbol(), null);
        assertTrue(fetched.isPresent());
        assertEquals(stock.getMarket(), fetched.get().getMarket());
        assertEquals(stock.getSymbol(), fetched.get().getSymbol());
        assertEquals(stock.getName(), fetched.get().getName());
        assertEquals(stockPrice1.getTimestamp(), fetched.get().getTimestamp());
        assertEquals(stockPrice1.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testGetForMarket() {
        Results<PricedStock> results = pricedStockService.getForMarket(TWITTER, null, new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForMarketSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));

        Results<PricedStock> results = pricedStockService.getForMarket(TWITTER, null, new Page(), emptySet());
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
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), NAME.toSort()));
        Results<PricedStock> results = pricedStockService.getForMarket(TWITTER, null, new Page(), sort);
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
    public void testGetForMarketSomeMixedActive() {
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

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));
        assertEquals(1, stockPriceService.add(stock3Price1));
        assertEquals(1, stockPriceService.add(stock3Price2));

        Results<PricedStock> nullActive = pricedStockService.getForMarket(TWITTER, null, new Page(), emptySet());
        assertEquals(3, nullActive.getTotal());
        assertEquals(3, nullActive.getResults().size());
        Results<PricedStock> trueActive = pricedStockService.getForMarket(TWITTER, true, new Page(), emptySet());
        assertEquals(2, trueActive.getTotal());
        assertEquals(2, trueActive.getResults().size());
        Results<PricedStock> falseActive = pricedStockService.getForMarket(TWITTER, false, new Page(), emptySet());
        assertEquals(1, falseActive.getTotal());
        assertEquals(1, falseActive.getResults().size());
    }

    @Test
    public void testConsumeForMarket() {
        List<PricedStock> results = new ArrayList<>();
        assertEquals(0, pricedStockService.consumeForMarket(TWITTER, null, results::add, emptySet()));
        assertTrue(results.isEmpty());
    }

    @Test
    public void testConsumeForMarketSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));

        List<PricedStock> results = new ArrayList<>();
        assertEquals(2, pricedStockService.consumeForMarket(TWITTER, null, results::add, emptySet()));
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
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));

        List<PricedStock> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), PRICE.toSort()));
        assertEquals(2, pricedStockService.consumeForMarket(TWITTER, null, results::add, sort));
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
    public void testConsumeForMarketMixedActive() {
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

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));
        assertEquals(1, stockPriceService.add(stock3Price1));
        assertEquals(1, stockPriceService.add(stock3Price2));

        List<PricedStock> nullResults = new ArrayList<>();
        assertEquals(3, pricedStockService.consumeForMarket(TWITTER, null, nullResults::add, emptySet()));
        assertEquals(3, nullResults.size());

        List<PricedStock> trueResults = new ArrayList<>();
        assertEquals(2, pricedStockService.consumeForMarket(TWITTER, true, trueResults::add, emptySet()));
        assertEquals(2, trueResults.size());

        List<PricedStock> falseResults = new ArrayList<>();
        assertEquals(1, pricedStockService.consumeForMarket(TWITTER, false, falseResults::add, emptySet()));
        assertEquals(1, falseResults.size());
    }

    @Test
    public void testGetAllNone() {
        Results<PricedStock> results = pricedStockService.getAll(null, new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));

        Results<PricedStock> results = pricedStockService.getAll(null, new Page(), emptySet());
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
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), PRICE.toSort()));
        Results<PricedStock> results = pricedStockService.getAll(null, new Page(), sort);
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
    public void testGetAllMixedActive() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(YOUTUBE).setSymbol("sym3").setName("name3").setActive(false);
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock3Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock3.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock3Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock3.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));
        assertEquals(1, stockPriceService.add(stock3Price1));
        assertEquals(1, stockPriceService.add(stock3Price2));

        Results<PricedStock> nullResults = pricedStockService.getAll(null, new Page(), emptySet());
        assertEquals(3, nullResults.getTotal());
        assertEquals(3, nullResults.getResults().size());

        Results<PricedStock> trueResults = pricedStockService.getAll(true, new Page(), emptySet());
        assertEquals(2, trueResults.getTotal());
        assertEquals(2, trueResults.getResults().size());

        Results<PricedStock> falseResults = pricedStockService.getAll(false, new Page(), emptySet());
        assertEquals(1, falseResults.getTotal());
        assertEquals(1, falseResults.getResults().size());
    }

    @Test
    public void testConsumeNone() {
        List<PricedStock> list = new ArrayList<>();
        assertEquals(0, pricedStockService.consume(null, list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));

        List<PricedStock> results = new ArrayList<>();
        assertEquals(2, pricedStockService.consume(null, results::add, emptySet()));
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
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2");
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));

        List<PricedStock> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), PRICE.toSort()));
        assertEquals(2, pricedStockService.consume(null, results::add, sort));
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
    public void testConsumeMixedActive() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1").setActive(true);
        Stock stock2 = new Stock().setMarket(YOUTUBE).setSymbol("sym2").setName("name2").setActive(true);
        Stock stock3 = new Stock().setMarket(YOUTUBE).setSymbol("sym3").setName("name2").setActive(false);
        StockPrice stock1Price1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock1Price2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock2Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock2Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);
        StockPrice stock3Price1 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock3.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stock3Price2 = new StockPrice().setMarket(YOUTUBE).setSymbol(stock3.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(8);

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));
        assertEquals(1, stockPriceService.add(stock1Price1));
        assertEquals(1, stockPriceService.add(stock1Price2));
        assertEquals(1, stockPriceService.add(stock2Price1));
        assertEquals(1, stockPriceService.add(stock2Price2));
        assertEquals(1, stockPriceService.add(stock3Price1));
        assertEquals(1, stockPriceService.add(stock3Price2));

        List<PricedStock> nullResults = new ArrayList<>();
        assertEquals(3, pricedStockService.consume(null, nullResults::add, emptySet()));
        assertEquals(3, nullResults.size());

        List<PricedStock> trueResults = new ArrayList<>();
        assertEquals(2, pricedStockService.consume(true, trueResults::add, emptySet()));
        assertEquals(2, trueResults.size());

        List<PricedStock> falseResults = new ArrayList<>();
        assertEquals(1, pricedStockService.consume(false, falseResults::add, emptySet()));
        assertEquals(1, falseResults.size());
    }

    @Test
    public void testAdd() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setTimestamp(now).setPrice(10);

        assertEquals(1, pricedStockService.add(pricedStock));

        Optional<Stock> stock = stockService.get(pricedStock.getMarket(), pricedStock.getSymbol(), null);
        Optional<StockPrice> stockPrice = stockPriceService.getLatest(pricedStock.getMarket(), pricedStock.getSymbol());

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

    @Test
    public void testAddInactive() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setTimestamp(now).setPrice(10).setActive(false);

        assertEquals(1, pricedStockService.add(pricedStock));

        Optional<Stock> stock = stockService.get(pricedStock.getMarket(), pricedStock.getSymbol(), null);
        Optional<StockPrice> stockPrice = stockPriceService.getLatest(pricedStock.getMarket(), pricedStock.getSymbol());

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

    @Test
    public void testAddAlreadyExists() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        PricedStock pricedStock = new PricedStock().setMarket(TWITTER).setSymbol("symbol").setName("Name").setTimestamp(now).setPrice(10);

        assertEquals(1, pricedStockService.add(pricedStock));
        assertEquals(0, pricedStockService.add(pricedStock));
    }
}
