package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.Page;
import vstocks.model.Results;
import vstocks.model.Sort;
import vstocks.model.Stock;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static vstocks.model.DatabaseField.NAME;
import static vstocks.model.DatabaseField.SYMBOL;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.SortDirection.DESC;

public class StockServiceImplIT extends BaseServiceImplIT {
    private StockService stockService;

    private final Stock stock1 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("symbol1")
            .setName("name1")
            .setProfileImage("link1");
    private final Stock stock2 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("symbol2")
            .setName("name2")
            .setProfileImage("link2");
    private final Stock stock3 = new Stock()
            .setMarket(YOUTUBE)
            .setSymbol("symbol3")
            .setName("name3")
            .setProfileImage("link3");

    @Before
    public void setup() {
        stockService = new StockServiceImpl(dataSourceExternalResource.get());

        // Clean out the stocks added via flyway
        stockService.truncate();
    }

    @After
    public void cleanup() {
        stockService.truncate();
    }

    @Test
    public void testGetMissing() {
        assertFalse(stockService.get(TWITTER, "missing").isPresent());
    }

    @Test
    public void testGetExists() {
        assertEquals(1, stockService.add(stock1));

        Stock fetched = stockService.get(stock1.getMarket(), stock1.getSymbol()).orElse(null);
        assertEquals(stock1, fetched);
    }

    @Test
    public void testGetForMarketNone() {
        Results<Stock> results = stockService.getForMarket(TWITTER, new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForMarketSomeNoSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        Results<Stock> results = stockService.getForMarket(TWITTER, new Page(), emptyList());
        validateResults(results, stock1, stock2);
    }

    @Test
    public void testGetForMarketSomeWithSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), NAME.toSort());
        Results<Stock> results = stockService.getForMarket(TWITTER, new Page(), sort);
        validateResults(results, stock2, stock1);
    }

    @Test
    public void testConsumeForMarketNone() {
        List<Stock> results = new ArrayList<>();
        assertEquals(0, stockService.consumeForMarket(TWITTER, results::add, emptyList()));
        validateResults(results);
    }

    @Test
    public void testConsumeForMarketSomeNoSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        List<Stock> results = new ArrayList<>();
        assertEquals(2, stockService.consumeForMarket(TWITTER, results::add, emptyList()));
        validateResults(results, stock1, stock2);
    }

    @Test
    public void testConsumeForMarketSomeWithSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), NAME.toSort());
        List<Stock> results = new ArrayList<>();
        assertEquals(2, stockService.consumeForMarket(TWITTER, results::add, sort));
        validateResults(results, stock2, stock1);
    }

    @Test
    public void testGetAllNone() {
        Results<Stock> results = stockService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSomeNoSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));

        Results<Stock> results = stockService.getAll(new Page(), emptyList());
        validateResults(results, stock1, stock2, stock3);
    }

    @Test
    public void testGetAllSomeWithSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), NAME.toSort());
        Results<Stock> results = stockService.getAll(new Page(), sort);
        validateResults(results, stock3, stock2, stock1);
    }

    @Test
    public void testConsumeNone() {
        List<Stock> results = new ArrayList<>();
        assertEquals(0, stockService.consume(results::add, emptyList()));
        validateResults(results);
    }

    @Test
    public void testConsumeSomeNoSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        List<Stock> results = new ArrayList<>();
        assertEquals(2, stockService.consume(results::add, emptyList()));
        validateResults(results, stock1, stock2);
    }

    @Test
    public void testConsumeSomeWithSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), NAME.toSort());
        List<Stock> results = new ArrayList<>();
        assertEquals(2, stockService.consume(results::add, sort));
        validateResults(results, stock2, stock1);
    }

    @Test
    public void testAdd() {
        assertEquals(1, stockService.add(stock1));

        Stock fetched = stockService.get(stock1.getMarket(), stock1.getSymbol()).orElse(null);
        assertEquals(stock1, fetched);
    }

    @Test
    public void testAddNoProfileImage() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));

        Stock fetched = stockService.get(TWITTER, stock.getSymbol()).orElse(null);
        assertEquals(stock, fetched);
    }

    @Test
    public void testAddConflictSameName() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setProfileImage("link");
        assertEquals(1, stockService.add(stock));
        assertEquals(0, stockService.add(stock));

        Stock fetched = stockService.get(stock.getMarket(), stock.getSymbol()).orElse(null);
        assertEquals(stock, fetched);
    }

    @Test
    public void testAddConflictDifferentName() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setProfileImage("link");
        assertEquals(1, stockService.add(stock));
        stock.setName("updated");
        assertEquals(1, stockService.add(stock));

        Stock fetched = stockService.get(stock.getMarket(), stock.getSymbol()).orElse(null);
        assertEquals(stock, fetched);
    }

    @Test
    public void testAddConflictDifferentProfileImage() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setProfileImage("link");
        assertEquals(1, stockService.add(stock));
        stock.setProfileImage("updated");
        assertEquals(1, stockService.add(stock));

        Stock fetched = stockService.get(stock.getMarket(), stock.getSymbol()).orElse(null);
        assertEquals(stock, fetched);
    }

    @Test
    public void testAddConflictNullProfileImage() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));
        stock.setProfileImage("updated");
        assertEquals(1, stockService.add(stock));

        Stock fetched = stockService.get(stock.getMarket(), stock.getSymbol()).orElse(null);
        assertEquals(stock, fetched);
    }

    @Test
    public void testAddConflictUpdateProfileImageToNull() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setProfileImage("link");
        assertEquals(1, stockService.add(stock));
        stock.setProfileImage(null);
        assertEquals(1, stockService.add(stock));

        Stock fetched = stockService.get(TWITTER, stock.getSymbol()).orElse(null);
        assertEquals(stock, fetched);
    }

    @Test
    public void testUpdateMissing() {
        assertEquals(0, stockService.update(stock1));
    }

    @Test
    public void testUpdate() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setProfileImage("link");
        assertEquals(1, stockService.add(stock));

        stock.setName("updated");
        stock.setProfileImage("updated");
        assertEquals(1, stockService.update(stock));

        Stock fetched = stockService.get(stock.getMarket(), stock.getSymbol()).orElse(null);
        assertEquals(stock, fetched);
    }

    @Test
    public void testUpdateNullProfileImageToSetNonNull() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name");
        assertEquals(1, stockService.add(stock));

        stock.setProfileImage("link");
        assertEquals(1, stockService.update(stock));

        Stock fetched = stockService.get(stock.getMarket(), stock.getSymbol()).orElse(null);
        assertEquals(stock, fetched);
    }

    @Test
    public void testUpdateProfileImageToSetNull() {
        Stock stock = new Stock().setMarket(TWITTER).setSymbol("sym").setName("name").setProfileImage("link");
        assertEquals(1, stockService.add(stock));

        stock.setProfileImage(null);
        assertEquals(1, stockService.update(stock));

        Stock updated = stockService.get(TWITTER, stock.getSymbol()).orElse(null);
        assertEquals(stock, updated);
    }

    @Test
    public void testDeleteMissing() {
        assertEquals(0, stockService.delete(TWITTER, "missing"));
    }

    @Test
    public void testDelete() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.delete(stock1.getMarket(), stock1.getSymbol()));
        assertFalse(stockService.get(stock1.getMarket(), stock1.getSymbol()).isPresent());
    }

    @Test
    public void testTruncate() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.truncate());
        assertFalse(stockService.get(stock1.getMarket(), stock1.getSymbol()).isPresent());
    }
}
