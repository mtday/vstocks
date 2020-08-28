package vstocks.rest.resource.market.stock;

import vstocks.db.ServiceFactory;
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
    private final ServiceFactory serviceFactory;

    @Inject
    public GetStock(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public PricedStock getStock(@PathParam("market") String marketStr, @PathParam("symbol") String symbol) {
        Market market = Market.from(marketStr)
                .orElseThrow(() -> new NotFoundException("Market " + marketStr + " not found"));
        return serviceFactory.getPricedStockService().get(market, symbol)
                .orElseThrow(() -> new NotFoundException("Stock " + market + "/" + symbol + " not found"));
    }
}
