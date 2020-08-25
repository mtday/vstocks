package vstocks.rest.resource.market.stock;

import vstocks.db.ServiceFactory;
import vstocks.model.Market;
import vstocks.model.Results;
import vstocks.model.StockPriceChange;
import vstocks.rest.resource.BaseResource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/market/{market}/stocks/best")
@Singleton
public class GetBestStocksForMarket extends BaseResource {
    private final ServiceFactory serviceFactory;

    @Inject
    public GetBestStocksForMarket(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Results<StockPriceChange> getBestStocks(@PathParam("market") String marketStr,
                                                   @QueryParam("pageNum") Integer pageNum,
                                                   @QueryParam("pageSize") Integer pageSize,
                                                   @QueryParam("sort") String sort) {
            Market market = Market.from(marketStr)
                .orElseThrow(() -> new NotFoundException("Market " + marketStr + " not found"));
        return serviceFactory.getStockPriceChangeService()
                .getForMarket(market, getPage(pageNum, pageSize), getSort(sort));
    }
}
