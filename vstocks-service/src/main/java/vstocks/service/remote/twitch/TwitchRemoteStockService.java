package vstocks.service.remote.twitch;

import vstocks.model.Stock;
import vstocks.model.StockPrice;
import vstocks.service.remote.RemoteStockService;

import java.util.List;

import static java.util.Collections.emptyList;

public class TwitchRemoteStockService implements RemoteStockService {
    @Override
    public void update(Stock stock, StockPrice stockPrice) {
    }

    @Override
    public List<Stock> search(String search) {
        return emptyList();
    }
}
