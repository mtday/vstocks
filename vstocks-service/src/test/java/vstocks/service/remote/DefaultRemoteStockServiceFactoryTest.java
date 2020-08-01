package vstocks.service.remote;

import org.junit.Test;
import vstocks.model.Market;

import static org.junit.Assert.assertNotNull;

public class DefaultRemoteStockServiceFactoryTest {
    @Test
    public void test() {
        RemoteStockServiceFactory remoteStockServiceFactory = new DefaultRemoteStockServiceFactory();

        for (Market market : Market.values()) {
            assertNotNull(remoteStockServiceFactory.getForMarket(market));
        }
    }
}
