package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.*;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.Assert.*;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.Market.YOUTUBE;
import static vstocks.model.SortDirection.DESC;

public class PricedStockServiceImplIT extends BaseServiceImplIT {
    private StockService stockService;
    private StockPriceService stockPriceService;
    private PricedStockService pricedStockService;

    private final Stock stock1 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("sym1")
            .setName("name1")
            .setProfileImage("link1");
    private final Stock stock2 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("sym2")
            .setName("name2")
            .setProfileImage("link2");
    private final Stock stock3 = new Stock()
            .setMarket(YOUTUBE)
            .setSymbol("sym3")
            .setName("name3")
            .setProfileImage("link3");

    private final StockPrice stockPrice11 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now)
            .setPrice(10);
    private final StockPrice stockPrice12 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(8);
    private final StockPrice stockPrice21 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now)
            .setPrice(10);
    private final StockPrice stockPrice22 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(12);
    private final StockPrice stockPrice31 = new StockPrice()
            .setMarket(stock3.getMarket())
            .setSymbol(stock3.getSymbol())
            .setTimestamp(now)
            .setPrice(10);
    private final StockPrice stockPrice32 = new StockPrice()
            .setMarket(stock3.getMarket())
            .setSymbol(stock3.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(10);

    private final PricedStock pricedStock1 = new PricedStock()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setName(stock1.getName())
            .setProfileImage(stock1.getProfileImage())
            .setTimestamp(stockPrice11.getTimestamp())
            .setPrice(stockPrice11.getPrice());
    private final PricedStock pricedStock2 = new PricedStock()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setName(stock2.getName())
            .setProfileImage(stock2.getProfileImage())
            .setTimestamp(stockPrice21.getTimestamp())
            .setPrice(stockPrice21.getPrice());
    private final PricedStock pricedStock3 = new PricedStock()
            .setMarket(stock3.getMarket())
            .setSymbol(stock3.getSymbol())
            .setName(stock3.getName())
            .setProfileImage(stock3.getProfileImage())
            .setTimestamp(stockPrice31.getTimestamp())
            .setPrice(stockPrice31.getPrice());

    @Before
    public void setup() {
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        pricedStockService = new PricedStockServiceImpl(dataSourceExternalResource.get());

        // Clear out the initial stocks from the Flyway script
        stockService.truncate();
    }

    @After
    public void cleanup() {
        stockPriceService.truncate();
        stockService.truncate();
    }

    @Test
    public void testGetMissing() {
        assertFalse(pricedStockService.get(TWITTER, "missing").isPresent());
    }

    @Test
    public void testGetExistsWithNoPrice() {
        assertEquals(1, stockService.add(stock1));

        Optional<PricedStock> fetched = pricedStockService.get(stock1.getMarket(), stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock1, fetched.get().asStock());
        assertNotNull(fetched.get().getTimestamp()); // defaults to Instant.now()
        assertEquals(1, fetched.get().getPrice()); // defaults to 1
    }

    @Test
    public void testGetExistsWithSinglePrice() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockPriceService.add(stockPrice11));

        Optional<PricedStock> fetched = pricedStockService.get(stock1.getMarket(), stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock1, fetched.get().asStock());
        assertEquals(stockPrice11, fetched.get().asStockPrice());
    }

    @Test
    public void testGetExistsWithMultiplePrices() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));

        Optional<PricedStock> fetched = pricedStockService.get(stock1.getMarket(), stock1.getSymbol());
        assertTrue(fetched.isPresent());
        assertEquals(stock1, fetched.get().asStock());
        assertEquals(stockPrice11, fetched.get().asStockPrice());
    }

    @Test
    public void testGetForMarketNone() {
        Results<PricedStock> results = pricedStockService.getForMarket(TWITTER, new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForMarketSomeNoSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));

        Results<PricedStock> results = pricedStockService.getForMarket(stock1.getMarket(), new Page(), emptyList());
        validateResults(results, pricedStock1, pricedStock2);
    }

    @Test
    public void testGetForMarketSomeWithSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), NAME.toSort());
        Results<PricedStock> results = pricedStockService.getForMarket(stock1.getMarket(), new Page(), sort);
        validateResults(results, pricedStock2, pricedStock1);
    }

    @Test
    public void testGetAllNone() {
        Results<PricedStock> results = pricedStockService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSomeNoSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));
        assertEquals(1, stockPriceService.add(stockPrice31));
        assertEquals(1, stockPriceService.add(stockPrice32));

        Results<PricedStock> results = pricedStockService.getAll(new Page(), emptyList());
        validateResults(results, pricedStock1, pricedStock2, pricedStock3);
    }

    @Test
    public void testGetAllSomeWithSort() {
        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockService.add(stock3));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));
        assertEquals(1, stockPriceService.add(stockPrice31));
        assertEquals(1, stockPriceService.add(stockPrice32));

        List<Sort> sort = asList(SYMBOL.toSort(DESC), PRICE.toSort());
        Results<PricedStock> results = pricedStockService.getAll(new Page(), sort);
        validateResults(results, pricedStock3, pricedStock2, pricedStock1);
    }

    @Test
    public void testAdd() {
        assertEquals(1, pricedStockService.add(pricedStock1));

        Optional<Stock> stock = stockService.get(pricedStock1.getMarket(), pricedStock1.getSymbol());
        Optional<StockPrice> stockPrice = stockPriceService.getLatest(pricedStock1.getMarket(), pricedStock1.getSymbol());
        assertTrue(stock.isPresent());
        assertTrue(stockPrice.isPresent());
        assertEquals(pricedStock1.asStock(), stock.get());
        assertEquals(pricedStock1.asStockPrice(), stockPrice.get());
    }

    @Test
    public void testAddNoProfileImage() {
        PricedStock pricedStock = new PricedStock()
                .setMarket(TWITTER)
                .setSymbol("symbol")
                .setName("Name")
                .setTimestamp(now)
                .setPrice(10);

        assertEquals(1, pricedStockService.add(pricedStock));

        Optional<Stock> stock = stockService.get(pricedStock.getMarket(), pricedStock.getSymbol());
        Optional<StockPrice> stockPrice = stockPriceService.getLatest(pricedStock.getMarket(), pricedStock.getSymbol());
        assertTrue(stock.isPresent());
        assertTrue(stockPrice.isPresent());

        assertEquals(pricedStock.asStock(), stock.get());
        assertEquals(pricedStock.asStockPrice(), stockPrice.get());

        assertNull(stock.get().getProfileImage());
    }

    @Test
    public void testAddAlreadyExists() {
        assertEquals(1, pricedStockService.add(pricedStock1));
        assertEquals(0, pricedStockService.add(pricedStock1));

        Optional<Stock> stock = stockService.get(pricedStock1.getMarket(), pricedStock1.getSymbol());
        Optional<StockPrice> stockPrice = stockPriceService.getLatest(pricedStock1.getMarket(), pricedStock1.getSymbol());
        assertTrue(stock.isPresent());
        assertTrue(stockPrice.isPresent());
        assertEquals(pricedStock1.asStock(), stock.get());
        assertEquals(pricedStock1.asStockPrice(), stockPrice.get());
    }
}
