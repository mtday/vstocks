package vstocks.rest.task;

import org.junit.Test;
import org.mockito.stubbing.Answer;
import vstocks.db.DBFactory;
import vstocks.db.OwnedStockDB;
import vstocks.db.StockDB;
import vstocks.db.StockPriceDB;
import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.rest.Environment;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.remote.RemoteStockService;
import vstocks.service.remote.RemoteStockServiceFactory;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class StockUpdateTaskTest {
    @Test
    public void testSchedule() {
        Environment environment = mock(Environment.class);
        ExecutorService executorService = mock(ExecutorService.class);
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        new StockUpdateTask(environment, executorService).schedule(scheduledExecutorService);
        verify(scheduledExecutorService, times(1)).scheduleAtFixedRate(
                any(StockUpdateTask.class), anyLong(), anyLong(), any(TimeUnit.class));
    }

    @Test
    public void testRun() throws InterruptedException {
        RemoteStockService remoteStockService = mock(RemoteStockService.class);
        when(remoteStockService.getUpdateRunnable(any(), any())).thenAnswer((Answer<StockUpdateRunnable>) invocation -> {
            Consumer<PricedStock> consumer = invocation.getArgument(1);
            return new StockUpdateRunnable() {
                @Override
                public void accept(Stock stock) {
                    PricedStock pricedStock = new PricedStock()
                            .setMarket(stock.getMarket())
                            .setSymbol(stock.getSymbol())
                            .setName(stock.getName())
                            .setImageLink(stock.getImageLink())
                            .setTimestamp(Instant.now())
                            .setPrice(10);
                    consumer.accept(pricedStock);
                }

                @Override
                public void run() {
                }
            };
        });

        RemoteStockServiceFactory remoteStockServiceFactory = mock(RemoteStockServiceFactory.class);
        when(remoteStockServiceFactory.getForMarket(any())).thenReturn(remoteStockService);

        StockDB stockDB = mock(StockDB.class);
        List<Stock> updatedStocks = new ArrayList<>();
        when(stockDB.update(any())).thenAnswer((Answer<Integer>) invocation -> {
            updatedStocks.add(invocation.getArgument(0));
            return 1;
        });

        StockPriceDB stockPriceDB = mock(StockPriceDB.class);
        List<StockPrice> addedStockPrices = new ArrayList<>();
        when(stockPriceDB.add(any())).thenAnswer((Answer<Integer>) invocation -> {
            addedStockPrices.add(invocation.getArgument(0));
            return 1;
        });

        OwnedStockDB ownedStockDB = mock(OwnedStockDB.class);
        when(ownedStockDB.consumeForMarket(any(), any(), any())).thenAnswer((Answer<Integer>) invocation -> {
            Market market = invocation.getArgument(0);
            Consumer<Stock> consumer = invocation.getArgument(1);
            consumer.accept(new Stock().setMarket(market).setSymbol("s1").setName("s1").setImageLink("link"));
            consumer.accept(new Stock().setMarket(market).setSymbol("s2").setName("s2").setImageLink("link"));
            return 2;
        });

        DBFactory dbFactory = mock(DBFactory.class);
        when(dbFactory.getOwnedStockDB()).thenReturn(ownedStockDB);
        when(dbFactory.getStockDB()).thenReturn(stockDB);
        when(dbFactory.getStockPriceDB()).thenReturn(stockPriceDB);

        Environment environment = mock(Environment.class);
        when(environment.getRemoteStockServiceFactory()).thenReturn(remoteStockServiceFactory);
        when(environment.getDBFactory()).thenReturn(dbFactory);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        new StockUpdateTask(environment, executorService).run();
        executorService.shutdown();

        while (!executorService.isTerminated()) {
            executorService.awaitTermination(10, MILLISECONDS);
        }

        List<String> expectedStocks = asList(
                "TWITTER:s1", "TWITTER:s2",
                "YOUTUBE:s1", "YOUTUBE:s2",
                "INSTAGRAM:s1", "INSTAGRAM:s2",
                "TWITCH:s1", "TWITCH:s2",
                "FACEBOOK:s1", "FACEBOOK:s2"
        );
        List<String> stocks = updatedStocks.stream()
                .map(stock -> format("%s:%s", stock.getMarket(), stock.getSymbol()))
                .collect(toList());
        assertEquals(join("\n", expectedStocks), join("\n", stocks));

        List<String> expectedStockPrices = asList(
                "TWITTER:s1:10", "TWITTER:s2:10",
                "YOUTUBE:s1:10", "YOUTUBE:s2:10",
                "INSTAGRAM:s1:10", "INSTAGRAM:s2:10",
                "TWITCH:s1:10", "TWITCH:s2:10",
                "FACEBOOK:s1:10", "FACEBOOK:s2:10"
        );
        List<String> stockPrices = addedStockPrices.stream()
                .map(price -> format("%s:%s:%d", price.getMarket(), price.getSymbol(), price.getPrice()))
                .collect(toList());
        assertEquals(join("\n", expectedStockPrices), join("\n", stockPrices));
    }
}
