package vstocks.rest.resource.v1.market.stock;

import vstocks.model.Market;
import vstocks.model.PricedStock;
import vstocks.rest.resource.BaseResource;
import vstocks.service.remote.RemoteStockService;
import vstocks.service.remote.RemoteStockServiceFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import java.util.List;

import static java.util.Optional.ofNullable;
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
    public List<PricedStock> searchStocks(@PathParam("market") String marketId,
                                          @QueryParam("q") String search) {
        String symbol = ofNullable(search)
                .orElseThrow(() -> new BadRequestException("Missing required 'q' query parameter"));
        Market market = Market.from(marketId)
                .orElseThrow(() -> new NotFoundException("Market " + marketId + " not found"));
        RemoteStockService remoteStockService = remoteStockServiceFactory.getForMarket(market);
        return remoteStockService.search(symbol, SEARCH_RESULT_LIMIT);
    }
}
