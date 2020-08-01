package vstocks.service.remote;

import vstocks.model.Market;

public interface RemoteStockServiceFactory {
    RemoteStockService getForMarket(Market market);
}
