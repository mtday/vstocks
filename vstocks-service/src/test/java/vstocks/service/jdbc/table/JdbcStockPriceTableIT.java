package vstocks.service.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.service.DataSourceExternalResource;
import vstocks.model.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;

public class JdbcStockPriceTableIT {
    @ClassRule
    public static DataSourceExternalResource dataSourceExternalResource = new DataSourceExternalResource();

    private MarketTable marketTable;
    private StockTable stockTable;
    private StockPriceTable stockPriceTable;

    private final Market market = new Market().setId("id").setName("name");
    private final Stock stock1 = new Stock().setId("id1").setMarketId(market.getId()).setSymbol("sym1").setName("name1");
    private final Stock stock2 = new Stock().setId("id2").setMarketId(market.getId()).setSymbol("sym2").setName("name2");

    @Before
    public void setup() throws SQLException {
        marketTable = new MarketTable();
        stockTable = new StockTable();
        stockPriceTable = new StockPriceTable();

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
    public void testGetMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(stockPriceTable.get(connection, "missing-id").isPresent());
        }
    }

    @Test
    public void testGetExists() throws SQLException {
        StockPrice stockPrice = new StockPrice().setId("id").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockPriceTable.add(connection, stockPrice));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Optional<StockPrice> fetched = stockPriceTable.get(connection, stockPrice.getId());
            assertTrue(fetched.isPresent());
            assertEquals(stockPrice.getMarketId(), fetched.get().getMarketId());
            assertEquals(stockPrice.getStockId(), fetched.get().getStockId());
            assertEquals(stockPrice.getTimestamp().toEpochMilli(), fetched.get().getTimestamp().toEpochMilli());
            assertEquals(stockPrice.getPrice(), fetched.get().getPrice());
        }
    }

    @Test
    public void testGetLatestNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<StockPrice> results = stockPriceTable.getLatest(connection, singleton(stock1.getId()), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetLatestSome() throws SQLException {
        StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(12);
        StockPrice stockPrice3 = new StockPrice().setId("id3").setMarketId(market.getId()).setStockId(stock2.getId()).setTimestamp(Instant.now()).setPrice(20);
        StockPrice stockPrice4 = new StockPrice().setId("id4").setMarketId(market.getId()).setStockId(stock2.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(18);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice3));
            assertEquals(1, stockPriceTable.add(connection, stockPrice4));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<StockPrice> results = stockPriceTable.getLatest(connection, asList(stock1.getId(), stock2.getId()), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(stockPrice1));
            assertTrue(results.getResults().contains(stockPrice3));
        }
    }

    @Test
    public void testGetForStockNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<StockPrice> results = stockPriceTable.getForStock(connection, stock1.getId(), new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForStockSome() throws SQLException {
        StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<StockPrice> results = stockPriceTable.getForStock(connection, stock1.getId(), new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(stockPrice1));
            assertTrue(results.getResults().contains(stockPrice2));
        }
    }

    @Test
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<StockPrice> results = stockPriceTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
        StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(12);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            connection.commit();
        }

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<StockPrice> results = stockPriceTable.getAll(connection, new Page());
            assertEquals(2, results.getTotal());
            assertEquals(2, results.getResults().size());
            assertTrue(results.getResults().contains(stockPrice1));
            assertTrue(results.getResults().contains(stockPrice2));
        }
    }

    @Test
    public void testAdd() throws SQLException {
        StockPrice stockPrice = new StockPrice().setId("id").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockPriceTable.add(connection, stockPrice));
            connection.commit();
        }
    }

    @Test(expected = Exception.class)
    public void testAddConflict() throws SQLException {
        StockPrice stockPrice = new StockPrice().setId("id").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockPriceTable.add(connection, stockPrice));
            stockPriceTable.add(connection, stockPrice);
        }
    }

    @Test
    public void testDeleteMissing() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(0, stockPriceTable.delete(connection, "missing-id"));
        }
    }

    @Test
    public void testDelete() throws SQLException {
        StockPrice stockPrice = new StockPrice().setId("id").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockPriceTable.add(connection, stockPrice));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockPriceTable.delete(connection, stockPrice.getId()));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertFalse(stockPriceTable.get(connection, stockPrice.getId()).isPresent());
        }
    }

    @Test
    public void testAgeOff() throws SQLException {
        StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(10);
        StockPrice stockPrice3 = new StockPrice().setId("id3").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(20)).setPrice(10);

        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice3));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(2, stockPriceTable.ageOff(connection, Instant.now().minusSeconds(5)));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<StockPrice> results = stockPriceTable.getAll(connection, new Page());
            assertEquals(1, results.getTotal());
            assertEquals(1, results.getResults().size());
            assertEquals(stockPrice1, results.getResults().iterator().next());
        }
    }

    @Test
    public void testTruncate() throws SQLException {
        StockPrice stockPrice1 = new StockPrice().setId("id1").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now()).setPrice(10);
        StockPrice stockPrice2 = new StockPrice().setId("id2").setMarketId(market.getId()).setStockId(stock1.getId()).setTimestamp(Instant.now().minusSeconds(10)).setPrice(12);
        StockPrice stockPrice3 = new StockPrice().setId("id3").setMarketId(market.getId()).setStockId(stock2.getId()).setTimestamp(Instant.now()).setPrice(1000);
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(1, stockPriceTable.add(connection, stockPrice1));
            assertEquals(1, stockPriceTable.add(connection, stockPrice2));
            assertEquals(1, stockPriceTable.add(connection, stockPrice3));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            assertEquals(3, stockPriceTable.truncate(connection));
            connection.commit();
        }
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<StockPrice> results = stockPriceTable.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }
}
