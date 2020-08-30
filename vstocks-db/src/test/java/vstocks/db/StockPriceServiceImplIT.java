package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.PRICE;
import static vstocks.model.DatabaseField.SYMBOL;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;

public class StockPriceServiceImplIT extends BaseServiceImplIT {
    private StockService stockService;
    private StockPriceService stockPriceService;

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

    private final StockPrice stockPrice11 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now)
            .setPrice(10);
    private final StockPrice stockPrice12 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(9);
    private final StockPrice stockPrice21 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now)
            .setPrice(10);
    private final StockPrice stockPrice22 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(11);

    @Before
    public void setup() {
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
    }

    @After
    public void cleanup() {
        stockPriceService.truncate();
        stockService.truncate();
    }

    @Test
    public void testGetLatestMissing() {
        assertFalse(stockPriceService.getLatest(TWITTER, "missing-id").isPresent());
    }

    @Test
    public void testGetLatestExists() {
        assertEquals(1, stockPriceService.add(stockPrice11));

        StockPrice fetched =
                stockPriceService.getLatest(stockPrice11.getMarket(), stockPrice11.getSymbol()).orElse(null);
        assertEquals(stockPrice11, fetched);
    }

    @Test
    public void testGetLatestNone() {
        Results<StockPrice> results =
                stockPriceService.getLatest(stock1.getMarket(), singleton(stock1.getSymbol()), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetLatestSomeNoSort() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));

        Results<StockPrice> results = stockPriceService.getLatest(
                stock1.getMarket(), asList(stock1.getSymbol(), stock2.getSymbol()), new Page(), emptyList());
        validateResults(results, stockPrice11, stockPrice21);
    }

    @Test
    public void testGetLatestSomeWithSort() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), PRICE.toSort());
        Results<StockPrice> results = stockPriceService.getLatest(
                stock1.getMarket(), asList(stock1.getSymbol(), stock2.getSymbol()), new Page(), sort);
        validateResults(results, stockPrice21, stockPrice11);
    }

    @Test
    public void testGetForStockNone() {
        Results<StockPrice> results = stockPriceService.getForStock(
                stock1.getMarket(), stock1.getSymbol(), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForStockSomeNoSort() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));

        Results<StockPrice> results =
                stockPriceService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), emptyList());
        validateResults(results, stockPrice11, stockPrice12);
    }

    @Test
    public void testGetForStockSomeWithSort() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));

        List<Sort> sort = asList(PRICE.toSort(), SYMBOL.toSort(DESC));
        Results<StockPrice> results =
                stockPriceService.getForStock(stock1.getMarket(), stock1.getSymbol(), new Page(), sort);
        validateResults(results, stockPrice12, stockPrice11);
    }

    @Test
    public void testGetAllNone() {
        Results<StockPrice> results = stockPriceService.getAll(new Page(), emptyList());
        assertEquals(Page.from(1, 20, 0, 0), results.getPage());
        assertTrue(results.getResults().isEmpty());
    }

    @Test
    public void testGetAllSomeNoSort() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));

        Results<StockPrice> results = stockPriceService.getAll(new Page(), emptyList());
        validateResults(results, stockPrice11, stockPrice12);
    }

    @Test
    public void testGetAllSomeWithSort() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));

        List<Sort> sort = asList(PRICE.toSort(), SYMBOL.toSort());
        Results<StockPrice> results = stockPriceService.getAll(new Page(), sort);
        validateResults(results, stockPrice12, stockPrice11);
    }

    @Test
    public void testConsumeNone() {
        List<StockPrice> results = new ArrayList<>();
        assertEquals(0, stockPriceService.consume(results::add, emptyList()));
        validateResults(results);
    }

    @Test
    public void testConsumeSomeNoSort() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));

        List<StockPrice> results = new ArrayList<>();
        assertEquals(2, stockPriceService.consume(results::add, emptyList()));
        validateResults(results, stockPrice11, stockPrice12);
    }

    @Test
    public void testConsumeSomeWithSort() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));

        List<StockPrice> results = new ArrayList<>();
        List<Sort> sort = asList(SYMBOL.toSort(), PRICE.toSort(DESC));
        assertEquals(2, stockPriceService.consume(results::add, sort));
        validateResults(results, stockPrice11, stockPrice12);
    }

    @Test
    public void testAdd() {
        assertEquals(1, stockPriceService.add(stockPrice11));
    }

    @Test
    public void testAddConflictSamePrice() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(0, stockPriceService.add(stockPrice11));

        StockPrice fetched =
                stockPriceService.getLatest(stockPrice11.getMarket(), stockPrice11.getSymbol()).orElse(null);
        assertEquals(stockPrice11, fetched);
    }

    @Test
    public void testAddConflictDifferentPrice() {
        StockPrice stockPrice = new StockPrice()
                .setMarket(stock1.getMarket())
                .setSymbol(stock1.getSymbol())
                .setTimestamp(now)
                .setPrice(10);
        assertEquals(1, stockPriceService.add(stockPrice));
        stockPrice.setPrice(12);
        assertEquals(1, stockPriceService.add(stockPrice));

        StockPrice fetched = stockPriceService.getLatest(stockPrice.getMarket(), stockPrice.getSymbol()).orElse(null);
        assertEquals(stockPrice, fetched);
    }

    @Test
    public void testAddAll() {
        assertEquals(2, stockPriceService.addAll(asList(stockPrice11, stockPrice21)));

        Results<StockPrice> results = stockPriceService.getLatest(
                stock1.getMarket(), asList(stock1.getSymbol(), stock2.getSymbol()), new Page(), emptyList());
        validateResults(results, stockPrice11, stockPrice21);
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));

        assertEquals(2, stockPriceService.ageOff(now.minusSeconds(5)));

        Results<StockPrice> results = stockPriceService.getAll(new Page(), emptyList());
        validateResults(results, stockPrice11, stockPrice21);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));

        assertEquals(4, stockPriceService.truncate());

        Results<StockPrice> results = stockPriceService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
