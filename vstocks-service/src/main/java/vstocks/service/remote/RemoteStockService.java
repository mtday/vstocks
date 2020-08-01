package vstocks.service.remote;

import vstocks.model.PricedStock;
import vstocks.service.StockUpdateRunnable;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public interface RemoteStockService {
    StockUpdateRunnable getUpdateRunnable(ExecutorService executorService,
                                          Consumer<PricedStock> updateConsumer);

    List<PricedStock> search(String search, int limit);
}
