package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Market;
import vstocks.model.Stock;
import vstocks.rest.resource.BaseResource;
import vstocks.service.db.DatabaseServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{market}/stock/{symbol}")
@Singleton
public class GetStock extends BaseResource {
    private final DatabaseServiceFactory databaseServiceFactory;

    @Inject
    public GetStock(DatabaseServiceFactory databaseServiceFactory) {
        this.databaseServiceFactory = databaseServiceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Stock getStock(@PathParam("market") String marketId,
                          @PathParam("symbol") String symbol) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));
        return databaseServiceFactory.getStockService().get(market, symbol)
                .orElseThrow(() -> new NotFoundException("Stock " + market + "/" + symbol + " not found"));
    }
}
