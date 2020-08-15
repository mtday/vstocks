package vstocks.rest.resource.market.stock;

import vstocks.db.DBFactory;
import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/market/{market}/stock/{symbol}")
@Singleton
public class GetStock extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public GetStock(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public PricedStock getStock(@PathParam("market") String marketId, @PathParam("symbol") String symbol) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));
        return dbFactory.getPricedStockDB().get(market, symbol)
                .orElseThrow(() -> new NotFoundException("Stock " + market + "/" + symbol + " not found"));
    }
}
