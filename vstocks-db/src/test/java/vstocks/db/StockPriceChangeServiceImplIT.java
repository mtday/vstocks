package vstocks.db;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import vstocks.model.*;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static vstocks.model.DatabaseField.*;
import static vstocks.model.Market.TWITTER;
import static vstocks.model.SortDirection.DESC;

public class StockPriceChangeServiceImplIT extends BaseServiceImplIT {
    private StockService stockService;
    private StockPriceService stockPriceService;
    private StockPriceChangeService stockPriceChangeService;

    private final Stock stock1 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("symbol1")
            .setName("Symbol1")
            .setProfileImage("link1");
    private final Stock stock2 = new Stock()
            .setMarket(TWITTER)
            .setSymbol("symbol2")
            .setName("Symbol2")
            .setProfileImage("link2");

    private final StockPrice stockPrice11 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now)
            .setPrice(11);
    private final StockPrice stockPrice12 = new StockPrice()
            .setMarket(stock1.getMarket())
            .setSymbol(stock1.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(12);
    private final StockPrice stockPrice21 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now)
            .setPrice(21);
    private final StockPrice stockPrice22 = new StockPrice()
            .setMarket(stock2.getMarket())
            .setSymbol(stock2.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(22);

    private final StockPriceChange stockPriceChange11 = new StockPriceChange()
            .setBatch(2)
            .setMarket(stockPrice11.getMarket())
            .setSymbol(stockPrice11.getSymbol())
            .setTimestamp(now)
            .setPrice(stockPrice11.getPrice())
            .setChange(stockPrice12.getPrice() - stockPrice11.getPrice())
            .setPercent(4.5f);
    private final StockPriceChange stockPriceChange12 = new StockPriceChange()
            .setBatch(1)
            .setMarket(stockPrice12.getMarket())
            .setSymbol(stockPrice12.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(stockPrice12.getPrice())
            .setChange(0)
            .setPercent(0f);
    private final StockPriceChange stockPriceChange21 = new StockPriceChange()
            .setBatch(2)
            .setMarket(stockPrice21.getMarket())
            .setSymbol(stockPrice21.getSymbol())
            .setTimestamp(now)
            .setPrice(stockPrice21.getPrice())
            .setChange(stockPrice22.getPrice() - stockPrice21.getPrice())
            .setPercent(4.5f);
    private final StockPriceChange stockPriceChange22 = new StockPriceChange()
            .setBatch(1)
            .setMarket(stockPrice22.getMarket())
            .setSymbol(stockPrice22.getSymbol())
            .setTimestamp(now.minusSeconds(10))
            .setPrice(stockPrice22.getPrice())
            .setChange(0)
            .setPercent(0f);

    @Before
    public void setup() {
        stockService = new StockServiceImpl(dataSourceExternalResource.get());
        stockPriceService = new StockPriceServiceImpl(dataSourceExternalResource.get());
        stockPriceChangeService = new StockPriceChangeServiceImpl(dataSourceExternalResource.get());

        assertEquals(1, stockService.add(stock1));
        assertEquals(1, stockService.add(stock2));
        assertEquals(1, stockPriceService.add(stockPrice11));
        assertEquals(1, stockPriceService.add(stockPrice12));
        assertEquals(1, stockPriceService.add(stockPrice21));
        assertEquals(1, stockPriceService.add(stockPrice22));
    }

    @After
    public void cleanup() {
        stockPriceChangeService.truncate();
        stockPriceService.truncate();
        stockService.truncate();
    }

    @Test
    public void testGenerateTie() {
        assertEquals(2, stockPriceChangeService.generate());

        Results<StockPriceChange> results = stockPriceChangeService.getAll(new Page(), emptyList());
        assertEquals(2, results.getTotal());
        assertEquals(2, results.getResults().size());
        assertEquals("21,11", results.getResults().stream().map(r -> "" + r.getPrice()).collect(joining(",")));
        assertEquals("-1,-1", results.getResults().stream().map(r -> "" + r.getChange()).collect(joining(",")));
        assertEquals("-4.5454545,-8.333333",
                results.getResults().stream().map(r -> "" + r.getPercent()).collect(joining(",")));
    }

    @Test
    public void testGetLatestNone() {
        StockPriceChangeCollection latest = stockPriceChangeService.getLatest(stock1.getMarket(), stock1.getSymbol());
        assertTrue(latest.getChanges().isEmpty());
    }

    @Test
    public void testGetLatestSome() {
        assertEquals(1, stockPriceChangeService.add(stockPriceChange11));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange12));

        StockPriceChangeCollection latest = stockPriceChangeService.getLatest(stock1.getMarket(), stock1.getSymbol());
        validateResults(latest.getChanges(), stockPriceChange11, stockPriceChange12);
    }

    @Test
    public void testGetForMarketNone() {
        Results<StockPriceChange> results =
                stockPriceChangeService.getForMarket(stock1.getMarket(), new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetForMarketSome() {
        assertEquals(1, stockPriceChangeService.add(stockPriceChange11));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange12));

        Results<StockPriceChange> results =
                stockPriceChangeService.getForMarket(stock1.getMarket(), new Page(), emptyList());
        validateResults(results, stockPriceChange11, stockPriceChange12);
    }

    @Test
    public void testGetForMarketSomeSort() {
        assertEquals(1, stockPriceChangeService.add(stockPriceChange11));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange12));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange21));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange22));

        List<Sort> sort = asList(CHANGE.toSort(DESC), TIMESTAMP.toSort());
        Results<StockPriceChange> results = stockPriceChangeService.getForMarket(stock1.getMarket(), new Page(), sort);
        validateResults(results, stockPriceChange11, stockPriceChange21, stockPriceChange12, stockPriceChange22);
    }

    @Test
    public void testGetAllNone() {
        Results<StockPriceChange> results = stockPriceChangeService.getAll(new Page(), emptyList());
        validateResults(results);
    }

    @Test
    public void testGetAllSome() {
        assertEquals(1, stockPriceChangeService.add(stockPriceChange11));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange12));

        Results<StockPriceChange> results = stockPriceChangeService.getAll(new Page(), emptyList());
        validateResults(results, stockPriceChange11, stockPriceChange12);
    }

    @Test
    public void testGetAllSomeSort() {
        assertEquals(1, stockPriceChangeService.add(stockPriceChange11));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange12));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange21));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange22));

        List<Sort> sort = asList(CHANGE.toSort(DESC), TIMESTAMP.toSort());
        Results<StockPriceChange> results = stockPriceChangeService.getAll(new Page(), sort);
        validateResults(results, stockPriceChange11, stockPriceChange21, stockPriceChange12, stockPriceChange22);
    }

    @Test
    public void testAddConflict() {
        assertEquals(1, stockPriceChangeService.add(stockPriceChange11));
        assertEquals(0, stockPriceChangeService.add(stockPriceChange11));
    }

    @Test
    public void testAgeOff() {
        assertEquals(1, stockPriceChangeService.add(stockPriceChange11));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange12));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange21));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange22));

        assertEquals(2, stockPriceChangeService.ageOff(now.minusSeconds(5)));

        Results<StockPriceChange> results = stockPriceChangeService.getAll(new Page(), emptyList());
        validateResults(results, stockPriceChange11, stockPriceChange21);
    }

    @Test
    public void testTruncate() {
        assertEquals(1, stockPriceChangeService.add(stockPriceChange11));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange12));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange21));
        assertEquals(1, stockPriceChangeService.add(stockPriceChange22));

        assertEquals(4, stockPriceChangeService.truncate());

        Results<StockPriceChange> results = stockPriceChangeService.getAll(new Page(), emptyList());
        validateResults(results);
    }
}
