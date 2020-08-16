package vstocks.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.db.DataSourceExternalResource;
import vstocks.db.jdbc.table.StockPriceTable;
import vstocks.db.jdbc.table.StockTable;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.PRICE;
import static vstocks.model.DatabaseField.SYMBOL;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;

public class JdbcStockPriceDBIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private StockTable stockTable;
    private StockPriceTable stockPriceTable;
    private JdbcStockPriceDB stockPriceDB;

    private final Stock stock1 = new Stock().setMarket(TWITTER).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setMarket(TWITTER).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        stockTable = new StockTable();
        stockPriceTable = new StockPriceTable();
        stockPriceDB = new JdbcStockPriceDB(dataSourceExternalResource.get());

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
        assertFalse(stockPriceDB.getLatest(TWITTER, "missing-id").isPresent());
    }

    @Test
    public void testGetLatestExists() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        assertEquals(1, stockPriceDB.add(stockPrice));

        StockPrice fetched = stockPriceDB.getLatest(stockPrice.getMarket(), stockPrice.getSymbol()).orElse(null);
        assertEquals(stockPrice, fetched);
    }

    @Test
    public void testGetLatestNone() {
        Results<StockPrice> results = stockPriceDB.getLatest(TWITTER, singleton(stock1.getSymbol()), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetLatestSomeNoSort() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        StockPrice stockPrice3 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(20);
        StockPrice stockPrice4 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(18);
        assertEquals(1, stockPriceDB.add(stockPrice1));
        assertEquals(1, stockPriceDB.add(stockPrice2));
        assertEquals(1, stockPriceDB.add(stockPrice3));
        assertEquals(1, stockPriceDB.add(stockPrice4));

        Results<StockPrice> results = stockPriceDB.getLatest(TWITTER, asList(stock1.getSymbol(), stock2.getSymbol()), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stockPrice1, results.getResults().get(0));
        assertEquals(stockPrice3, results.getResults().get(1));
    }

    @Test
    public void testGetLatestSomeWithSort() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        StockPrice stockPrice3 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(20);
        StockPrice stockPrice4 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(18);
        assertEquals(1, stockPriceDB.add(stockPrice1));
        assertEquals(1, stockPriceDB.add(stockPrice2));
        assertEquals(1, stockPriceDB.add(stockPrice3));
        assertEquals(1, stockPriceDB.add(stockPrice4));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), PRICE.toSort()));
        Results<StockPrice> results = stockPriceDB.getLatest(TWITTER, asList(stock1.getSymbol(), stock2.getSymbol()), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stockPrice3, results.getResults().get(0));
        assertEquals(stockPrice1, results.getResults().get(1));
    }

    @Test
    public void testGetForStockNone() {
        Results<StockPrice> results = stockPriceDB.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForStockSomeNoSort() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        assertEquals(1, stockPriceDB.add(stockPrice1));
        assertEquals(1, stockPriceDB.add(stockPrice2));

        Results<StockPrice> results = stockPriceDB.getForStock(TWITTER, stock1.getSymbol(), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stockPrice1, results.getResults().get(0));
        assertEquals(stockPrice2, results.getResults().get(1));
    }

    @Test
    public void testGetForStockSomeWithSort() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        assertEquals(1, stockPriceDB.add(stockPrice1));
        assertEquals(1, stockPriceDB.add(stockPrice2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(DESC), PRICE.toSort(DESC)));
        Results<StockPrice> results = stockPriceDB.getForStock(TWITTER, stock1.getSymbol(), new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stockPrice2, results.getResults().get(0));
        assertEquals(stockPrice1, results.getResults().get(1));
    }

    @Test
    public void testGetAllNone() {
        Results<StockPrice> results = stockPriceDB.getAll(new Page(), emptySet());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        assertEquals(1, stockPriceDB.add(stockPrice1));
        assertEquals(1, stockPriceDB.add(stockPrice2));

        Results<StockPrice> results = stockPriceDB.getAll(new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stockPrice1, results.getResults().get(0));
        assertEquals(stockPrice2, results.getResults().get(1));
    }

    @Test
    public void testGetAllSomeWithSort() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        assertEquals(1, stockPriceDB.add(stockPrice1));
        assertEquals(1, stockPriceDB.add(stockPrice2));

        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(), PRICE.toSort(DESC)));
        Results<StockPrice> results = stockPriceDB.getAll(new Page(), sort);
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stockPrice2, results.getResults().get(0));
        assertEquals(stockPrice1, results.getResults().get(1));
    }

    @Test
    public void testConsumeNone() {
        List<StockPrice> list = new ArrayList<>();
        assertEquals(0, stockPriceDB.consume(list::add, emptySet()));
        assertTrue(list.isEmpty());
    }

    @Test
    public void testConsumeSomeNoSort() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        assertEquals(1, stockPriceDB.add(stockPrice1));
        assertEquals(1, stockPriceDB.add(stockPrice2));

        List<StockPrice> results = new ArrayList<>();
        assertEquals(2, stockPriceDB.consume(results::add, emptySet()));
        assertEquals(2, results.size());
        assertEquals(stockPrice1, results.get(0));
        assertEquals(stockPrice2, results.get(1));
    }

    @Test
    public void testConsumeSomeWithSort() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(12);
        assertEquals(1, stockPriceDB.add(stockPrice1));
        assertEquals(1, stockPriceDB.add(stockPrice2));

        List<StockPrice> results = new ArrayList<>();
        Set<Sort> sort = new LinkedHashSet<>(asList(SYMBOL.toSort(), PRICE.toSort(DESC)));
        assertEquals(2, stockPriceDB.consume(results::add, sort));
        assertEquals(2, results.size());
        assertEquals(stockPrice2, results.get(0));
        assertEquals(stockPrice1, results.get(1));
    }

    @Test
    public void testAdd() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        assertEquals(1, stockPriceDB.add(stockPrice));
    }

    @Test
    public void testAddConflictSamePrice() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        assertEquals(1, stockPriceDB.add(stockPrice));
        assertEquals(0, stockPriceDB.add(stockPrice));

        StockPrice fetched = stockPriceDB.getLatest(stockPrice.getMarket(), stockPrice.getSymbol()).orElse(null);
        assertEquals(stockPrice, fetched);
    }

    @Test
    public void testAddConflictDifferentPrice() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        assertEquals(1, stockPriceDB.add(stockPrice));
        stockPrice.setPrice(12);
        assertEquals(1, stockPriceDB.add(stockPrice));

        StockPrice fetched = stockPriceDB.getLatest(stockPrice.getMarket(), stockPrice.getSymbol()).orElse(null);
        assertEquals(stockPrice, fetched);
    }

    @Test
    public void testAddAll() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock2.getSymbol()).setTimestamp(now).setPrice(20);
        assertEquals(2, stockPriceDB.addAll(asList(stockPrice1, stockPrice2)));

        Results<StockPrice> results = stockPriceDB.getLatest(TWITTER, asList(stock1.getSymbol(), stock2.getSymbol()), new Page(), emptySet());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals(stockPrice1, results.getResults().get(0));
        assertEquals(stockPrice2, results.getResults().get(1));
    }

    @Test
    public void testAgeOff() {
        Instant now = Instant.now().truncatedTo(SECONDS);
        StockPrice stockPrice1 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(10)).setPrice(10);
        StockPrice stockPrice3 = new StockPrice().setMarket(TWITTER).setSymbol(stock1.getSymbol()).setTimestamp(now.minusSeconds(20)).setPrice(10);

        assertEquals(1, stockPriceDB.add(stockPrice1));
        assertEquals(1, stockPriceDB.add(stockPrice2));
        assertEquals(1, stockPriceDB.add(stockPrice3));
        assertEquals(2, stockPriceDB.ageOff(now.minusSeconds(5)));

        Results<StockPrice> results = stockPriceDB.getAll(new Page(), emptySet());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(stockPrice1, results.getResults().iterator().next());
    }
}
