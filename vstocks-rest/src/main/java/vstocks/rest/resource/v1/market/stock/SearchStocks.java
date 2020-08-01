package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Market;
import vstocks.model.Stock;
import vstocks.rest.resource.BaseResource;
import vstocks.service.remote.RemoteStockService;
import vstocks.service.remote.RemoteStockServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v1/market/{market}/stocks/search")
@Singleton
public class SearchStocks extends BaseResource {
    private static final int SEARCH_RESULT_LIMIT = 20;

    private final RemoteStockServiceFactory remoteStockServiceFactory;

    @Inject
    public SearchStocks(RemoteStockServiceFactory remoteStockServiceFactory) {
        this.remoteStockServiceFactory = remoteStockServiceFactory;
    }

    @GET
    @Produces(APPLICATION_JSON)
    public List<Stock> searchStocks(@PathParam("market") String marketId,
                                    @QueryParam("q") String search) {
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));
        RemoteStockService remoteStockService = remoteStockServiceFactory.getForMarket(market);
        return remoteStockService.search(search, SEARCH_RESULT_LIMIT);
    }
}
