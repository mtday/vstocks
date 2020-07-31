package vstocks.service.remote;

import vstocks.model.Stock;
import vstocks.model.StockPrice;

import java.util.List;

public interface RemoteStockService {
    void update(Stock stock, StockPrice stockPrice);

    List<Stock> search(String search);
}
