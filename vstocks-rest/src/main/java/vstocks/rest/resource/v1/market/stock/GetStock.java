package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.rest.resource.BaseResource;
import vstocks.db.DBFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static java.util.Optional.ofNullable;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{market}/stock/{symbol}")
@Singleton
public class GetStock extends BaseResource {
    private final DBFactory dbFactory;

    @Inject
    public GetStock(DBFactory dbFactory) {
        this.dbFactory = dbFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public PricedStock getStock(@PathParam("market") String marketId,
                                @PathParam("symbol") String symbol,
                                @QueryParam("active") String activeParam) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));
        Boolean active = ofNullable(activeParam).map(Boolean::valueOf).orElse(null);
        return dbFactory.getPricedStockDB().get(market, symbol, active)
                .orElseThrow(() -> new NotFoundException("Stock " + market + "/" + symbol + " not found"));
    }
}
