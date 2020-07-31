package vstocks.service.remote.youtube;

import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.service.StockUpdateRunnable;
import vstocks.service.remote.RemoteStockService;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

public class YouTubeRemoteStockService implements RemoteStockService {
    @Override
    public StockUpdateRunnable getUpdateRunnable(ExecutorService executorService,
                                                 Consumer<Entry<Stock, StockPrice>> updateConsumer) {
        return new StockUpdateRunnable() {
            @Override
            public void accept(Stock stock) {
            }

            @Override
            public void run() {
            }

            @Override
            public void close() throws IOException {
            }
        };
    }

    @Override
    public List<Stock> search(String search) {
        return emptyList();
    }
}
