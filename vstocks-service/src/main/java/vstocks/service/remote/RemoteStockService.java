package vstocks.service.remote;

import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.service.StockUpdateRunnable;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public interface RemoteStockService {
    StockUpdateRunnable getUpdateRunnable(ExecutorService executorService,
                                          Consumer<Entry<Stock, StockPrice>> updateConsumer);

    List<Stock> search(String search, int limit);
}
