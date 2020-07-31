package vstocks.service.remote.twitch;

import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.remote.RemoteStockService;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

public class TwitchRemoteStockService implements RemoteStockService {
    @Override
    public StockUpdateRunnable getUpdateRunnable(ExecutorService executorService,
                                                 Consumer<Entry<Stock, StockPrice>> updateConsumer) {
        return null;
    }

    @Override
    public List<Stock> search(String search) {
        return emptyList();
    }
}
