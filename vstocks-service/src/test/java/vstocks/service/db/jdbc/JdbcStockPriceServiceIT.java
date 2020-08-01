package vstocks.service.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.service.db.DataSourceExternalResource;
import vstocks.service.db.jdbc.table.StockPriceTable;
import vstocks.service.db.jdbc.table.StockTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;
import static vstocks.model.Market.TWITTER;

public class JdbcStockPriceServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private StockTable stockTable;
    private StockPriceTable stockPriceTable;
    private JdbcStockPriceService stockPriceService;

    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        stockTable = new StockTable();
        stockPriceTable = new StockPriceTable();
        stockPriceService = new JdbcStockPriceService(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockTable.add(connection, stock1));
            assertEquals(1, stockTable.add(connection, stock2));
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
    public void testGetLatestMissing() {
        assertFalse(stockPriceService.getLatest(TWITTER, "missing-id").isPresent());
    }

    @Test
    public void testGetLatestExists() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        assertEquals(1, stockPriceService.add(stockPrice));

        Optional<StockPrice> fetched = stockPriceService.getLatest(TWITTER, stockPrice.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stockPrice.getMarket(), fetched.get().getMarket());
        assertEquals(stockPrice.getSymbol(), fetched.get().getSymbol());
        assertEquals(stockPrice.getTimestamp().toEpochMilli(), fetched.get().getTimestamp().toEpochMilli());
        assertEquals(stockPrice.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testGetLatestNone() {
        Results<StockPrice> results = stockPriceService.getLatest(TWITTER, singleton(stock1.getSymbol()), new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetLatestSome() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        StockPrice stockPrice3 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(20);
        StockPrice stockPrice4 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(18);
        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));
        assertEquals(1, stockPriceService.add(stockPrice3));
        assertEquals(1, stockPriceService.add(stockPrice4));

        Results<StockPrice> results = stockPriceService.getLatest(TWITTER, asList(stock1.getSymbol(), stock2.getSymbol()), new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(stockPrice1));
        assertTrue(results.getResults().contains(stockPrice3));
    }

    @Test
    public void testGetForStockNone() {
        Results<StockPrice> results = stockPriceService.getForStock(TWITTER, stock1.getSymbol(), new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForStockSome() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));

        Results<StockPrice> results = stockPriceService.getForStock(TWITTER, stock1.getSymbol(), new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(stockPrice1));
        assertTrue(results.getResults().contains(stockPrice2));
    }

    @Test
    public void testGetAllNone() {
        Results<StockPrice> results = stockPriceService.getAll(new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSome() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));

        Results<StockPrice> results = stockPriceService.getAll(new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(stockPrice1));
        assertTrue(results.getResults().contains(stockPrice2));
    }

    @Test
    public void testConsumeNone() {
        List<StockPrice> list = new ArrayList<>();
        assertEquals(0, stockPriceService.consume(list::add));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSome() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));

        List<StockPrice> list = new ArrayList<>();
        assertEquals(2, stockPriceService.consume(list::add));
        assertEquals(2, list.size());
        assertTrue(list.contains(stockPrice1));
        assertTrue(list.contains(stockPrice2));
    }

    @Test
    public void testAdd() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        assertEquals(1, stockPriceService.add(stockPrice));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        assertEquals(1, stockPriceService.add(stockPrice));
        stockPriceService.add(stockPrice);
    }

    @Test
    public void testAgeOff() {
        Instant now = Instant.now().truncatedTo(ChronoUnit.SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(10);
        StockPrice stockPrice3 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(20)).setPrice(10);

        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));
        assertEquals(1, stockPriceService.add(stockPrice3));
        assertEquals(2, stockPriceService.ageOff(now.minusSeconds(5)));

        Results<StockPrice> results = stockPriceService.getAll(new Page());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(stockPrice1, results.getResults().iterator().next());
    }
}
