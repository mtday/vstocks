package vstocks.rest.resource.market.stock;

import vstocks.db.ServiceFactory;
import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.model.Results;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/market/{marketId}/stocks")
@Singleton
public class GetStocksForMarket extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetStocksForMarket(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Results<PricedStock> getAllStocks(@PathParam("marketId") String marketId,
                                             @QueryParam("pageNum") Integer pageNum,
                                             @QueryParam("pageSize") Integer pageSize,
                                             @QueryParam("sort") String sort) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));
        return serviceFactory.getPricedStockService().getForMarket(market, getPage(pageNum, pageSize), getSort(sort));
    }
}
