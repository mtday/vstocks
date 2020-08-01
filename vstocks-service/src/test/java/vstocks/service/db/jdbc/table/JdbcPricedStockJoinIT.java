package vstocks.service.db.jdbc.table;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import vstocks.model.*;
import vstocks.service.db.DataSourceExternalResource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static vstocks.model.Market.TWITTER;

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
            assertFalse(pricedStockJoin.get(connection, TWITTER, "missing").isPresent());
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
            Optional<PricedStock> fetched = pricedStockJoin.get(connection, TWITTER, stock.getSymbol());
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
            Optional<PricedStock> fetched = pricedStockJoin.get(connection, TWITTER, stock.getSymbol());
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
            Optional<PricedStock> fetched = pricedStockJoin.get(connection, TWITTER, stock.getSymbol());
            assertTrue(fetched.isPresent());
            assertEquals(stock.getMarket(), fetched.get().getMarket());
            assertEquals(stock.getSymbol(), fetched.get().getSymbol());
            assertEquals(stock.getName(), fetched.get().getName());
            assertEquals(stockPrice1.getTimestamp(), fetched.get().getTimestamp());
            assertEquals(stockPrice1.getPrice(), fetched.get().getPrice());
        }
    }

    @Test
    public void testGetForMarket() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<PricedStock> results = pricedStockJoin.getForMarket(connection, TWITTER, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetForMarketSome() throws SQLException {
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
            Results<PricedStock> results = pricedStockJoin.getForMarket(connection, TWITTER, new Page());
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
    public void testConsumeForMarket() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<PricedStock> results = new ArrayList<>();
            assertEquals(0, pricedStockJoin.consumeForMarket(connection, TWITTER, results::add));
            assertTrue(results.isEmpty());
        }
    }

    @Test
    public void testConsumeForMarketSome() throws SQLException {
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
            assertEquals(2, pricedStockJoin.consumeForMarket(connection, TWITTER, results::add));
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
    public void testGetAllNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            Results<PricedStock> results = pricedStockJoin.getAll(connection, new Page());
            assertEquals(0, results.getTotal());
            assertTrue(results.getResults().isEmpty());
        }
    }

    @Test
    public void testGetAllSome() throws SQLException {
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
            Results<PricedStock> results = pricedStockJoin.getAll(connection, new Page());
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
    public void testConsumeNone() throws SQLException {
        try (Connection connection = dataSourceExternalResource.get().getConnection()) {
            List<PricedStock> list = new ArrayList<>();
            assertEquals(0, pricedStockJoin.consume(connection, list::add));
            assertTrue(list.isEmpty());
        }
    }

    @Test
    public void testConsumeSome() throws SQLException {
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
            assertEquals(2, pricedStockJoin.consume(connection, results::add));
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
}
