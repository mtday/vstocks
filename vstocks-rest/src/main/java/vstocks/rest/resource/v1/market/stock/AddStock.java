package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.rest.resource.BaseResource;
import vstocks.service.db.DatabaseServiceFactory;
import vstocks.service.remote.RemoteStockServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{market}/stock/{symbol}")
@Singleton
public class AddStock extends BaseResource {
    private final DatabaseServiceFactory databaseServiceFactory;
    private final RemoteStockServiceFactory remoteStockServiceFactory;

    @Inject
    public AddStock(DatabaseServiceFactory databaseServiceFactory,
                    RemoteStockServiceFactory remoteStockServiceFactory) {
        this.databaseServiceFactory = databaseServiceFactory;
        this.remoteStockServiceFactory = remoteStockServiceFactory;
    }

    @POST
    @Produces(APPLICATION_JSON)
    public PricedStock addStock(@PathParam("market") String marketId,
                                @PathParam("symbol") String symbol) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));

        List<PricedStock> stocks = remoteStockServiceFactory.getForMarket(market).search(symbol, 1);
        if (stocks.size() == 1) {
            PricedStock pricedStock = stocks.iterator().next();
            if (pricedStock.getSymbol().equalsIgnoreCase(symbol)) {
                databaseServiceFactory.getStockService().add(pricedStock.asStock());
                databaseServiceFactory.getStockPriceService().add(pricedStock.asStockPrice());
                return pricedStock;
            }
        }

        throw new NotFoundException("Stock " + market + "/" + symbol + " not found");
    }
}
