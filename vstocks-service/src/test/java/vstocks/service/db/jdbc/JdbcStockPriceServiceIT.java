package vstocks.service.db.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.service.db.DataSourceExternalResource;
import vstocks.service.db.jdbc.table.MarketTable;
import vstocks.service.db.jdbc.table.StockPriceTable;
import vstocks.service.db.jdbc.table.StockTable;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;

public class JdbcStockPriceServiceIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private MarketTable marketTable;
    private StockTable stockTable;
    private StockPriceTable stockPriceTable;
    private JdbcStockPriceService stockPriceService;

    private final Market market = new Market().setId("id").setName("name");
    private final Stock stock1 = new Stock().setId("id1").setMarketId(market.getId()).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setId("id2").setMarketId(market.getId()).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        marketTable = new MarketTable();
        stockTable = new StockTable();
        stockPriceTable = new StockPriceTable();
        stockPriceService = new JdbcStockPriceService(dataSourceExternalResource.get());

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, marketTable.add(connection, market));
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
            marketTable.truncate(connection);
            connection.commit();
        }
    }

    @Test
    public void testGetMissing() {
        assertFalse(stockPriceService.get("missing-id").isPresent());
    }

    @Test
    public void testGetExists() {
        StockPrice stockPrice = new StockPrice().setId("id").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        assertEquals(1, stockPriceService.add(stockPrice));

        Optional<StockPrice> fetched = stockPriceService.get(stockPrice.getId());
        assertTrue(fetched.isPresent());
        assertEquals(stockPrice.getMarketId(), fetched.get().getMarketId());
        assertEquals(stockPrice.getStockId(), fetched.get().getStockId());
        assertEquals(stockPrice.getTimestamp().toEpochMilli(), fetched.get().getTimestamp().toEpochMilli());
        assertEquals(stockPrice.getPrice(), fetched.get().getPrice());
    }

    @Test
    public void testGetLatestNone() {
        Results<StockPrice> results = stockPriceService.getLatest(singleton(stock1.getId()), new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetLatestSome() {
        StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(12);
        StockPrice stockPrice3 = new StockPrice().setId("id3").setMarketId(market.getId()).setStockId(stock2.getId()).setTimestamp(Instant.now()).setPrice(20);
        StockPrice stockPrice4 = new StockPrice().setId("id4").setMarketId(market.getId()).setStockId(stock2.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(18);
        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));
        assertEquals(1, stockPriceService.add(stockPrice3));
        assertEquals(1, stockPriceService.add(stockPrice4));

        Results<StockPrice> results = stockPriceService.getLatest(asList(stock1.getId(), stock2.getId()), new Page());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertTrue(results.getResults().contains(stockPrice1));
        assertTrue(results.getResults().contains(stockPrice3));
    }

    @Test
    public void testGetForStockNone() {
        Results<StockPrice> results = stockPriceService.getForStock(stock1.getId(), new Page());
        assertEquals(0, results.getTotal());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetForStockSome() {
        StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(12);
        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));

        Results<StockPrice> results = stockPriceService.getForStock(stock1.getId(), new Page());
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
        StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(12);
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
        StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(12);
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
        StockPrice stockPrice = new StockPrice().setId("id").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        assertEquals(1, stockPriceService.add(stockPrice));
    }

    @Test(expected = Exception.class)
    public void testAddConflict() {
        StockPrice stockPrice = new StockPrice().setId("id").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        assertEquals(1, stockPriceService.add(stockPrice));
        stockPriceService.add(stockPrice);
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, stockPriceService.delete("missing-id"));
    }

    @Test
    public void testDelete() {
        StockPrice stockPrice = new StockPrice().setId("id").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        assertEquals(1, stockPriceService.add(stockPrice));
        assertEquals(1, stockPriceService.delete(stockPrice.getId()));
        assertFalse(stockPriceService.get(stockPrice.getId()).isPresent());
    }

    @Test
    public void testAgeOff() {
        StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(10);
        StockPrice stockPrice3 = new StockPrice().setId("id3").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(20)).setPrice(10);

        assertEquals(1, stockPriceService.add(stockPrice1));
        assertEquals(1, stockPriceService.add(stockPrice2));
        assertEquals(1, stockPriceService.add(stockPrice3));
        assertEquals(2, stockPriceService.ageOff(Instant.now().minusSeconds(5)));

        Results<StockPrice> results = stockPriceService.getAll(new Page());
        assertEquals(1, results.getTotal());
        assertEquals(1, results.getResults().size());
        assertEquals(stockPrice1, results.getResults().iterator().next());
    }
}
