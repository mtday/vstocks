package vstocks.service.remote.youtube;

import vstocks.model.Stock;
import vstocks.service.remote.RemoteStockService;

import java.util.List;

import static java.util.Collections.emptyList;

public class YouTubeRemoteStockService implements RemoteStockService {
    @Override
    public int getPrice(Stock stock) {
        return 0;
    }

    @Override
    public void updatePrices(List<Stock> stocks) {

    }

    @Override
    public List<Stock> search(String search, int limit) {
        return emptyList();
    }
}
