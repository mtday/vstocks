package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Results;
import vstocks.model.Stock;
import vstocks.rest.resource.BaseResource;
import vstocks.service.ServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{marketId}/stocks")
@Singleton
public class GetStocksForMarket extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetStocksForMarket(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Results<Stock> getAllStocks(@PathParam("marketId") String marketId,
                                       @QueryParam("pageNum") Integer pageNum,
                                       @QueryParam("pageSize") Integer pageSize) {
        return serviceFactory.getStockService().getForMarket(marketId, getPage(pageNum, pageSize));
    }
}
