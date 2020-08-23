package vstocks.rest.resource.market.stock;

import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.rest.resource.BaseResource;
import vstocks.db.ServiceFactory;
import vstocks.service.remote.RemoteStockServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.WILDCARD;

@Path("/market/{market}/stock/{symbol}")
@Singleton
public class AddStock extends BaseResource {
    private final ServiceFactory dbFactory;
    private final RemoteStockServiceFactory remoteStockServiceFactory;

    @Inject
    public AddStock(ServiceFactory dbFactory, RemoteStockServiceFactory remoteStockServiceFactory) {
        this.dbFactory = dbFactory;
        this.remoteStockServiceFactory = remoteStockServiceFactory;
    }

    @POST
    @Produces(APPLICATION_JSON)
    @Consumes(WILDCARD)
    public PricedStock addStock(@PathParam("market") String marketId, @PathParam("symbol") String symbol) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));

        // We use 10 here as the limit to prevent problems with the symbol being a prefix for other valid
        // symbols. The RemoteStockService implementations should attempt to put the exact match symbol
        // first.
        PricedStock pricedStock = remoteStockServiceFactory.getForMarket(market).search(symbol, 10).stream()
                .filter(ps -> ps.getSymbol().equalsIgnoreCase(symbol))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Stock " + market + "/" + symbol + " not found"));

        dbFactory.getPricedStockService().add(pricedStock);
        return pricedStock;
    }
}
