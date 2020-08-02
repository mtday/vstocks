package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.rest.resource.BaseResource;
import vstocks.service.db.DatabaseServiceFactory;
import vstocks.service.remote.RemoteStockServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;

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
    @Consumes(WILDCARD)
    public PricedStock addStock(@PathParam("market") String marketId,
                                @PathParam("symbol") String symbol) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));

        // We use 10 here as the limit to prevent problems with the symbol being a prefix for other valid
        // symbols. The RemoteStockService implementations should attempt to put the exact match symbol
        // first.
        PricedStock pricedStock = remoteStockServiceFactory.getForMarket(market).search(symbol, 10).stream()
                .filter(ps -> ps.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Stock " + market + "/" + symbol + " not found"));

        databaseServiceFactory.getStockService().add(pricedStock.asStock());
        databaseServiceFactory.getStockPriceService().add(pricedStock.asStockPrice());
        return pricedStock;
    }
}
