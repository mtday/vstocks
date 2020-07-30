package vstocks.service.remote;

import vstocks.model.Stock;

import java.util.List;

public interface RemoteStockService {
    int getPrice(Stock stock);

    void updatePrices(List<Stock> stocks);

    List<Stock> search(String search, int limit);
}
