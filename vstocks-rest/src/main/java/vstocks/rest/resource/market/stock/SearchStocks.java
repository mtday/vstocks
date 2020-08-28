package vstocks.rest.resource.market.stock;

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

@Path("/market/{market}/stocks/search")
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
    public List<PricedStock> searchStocks(@PathParam("market") String marketStr, @QueryParam("q") String search) {
        Market market = Market.from(marketStr)
                .orElseThrow(() -> new NotFoundException("Market " + marketStr + " not found"));
        String symbol = ofNullable(search)
                .orElseThrow(() -> new BadRequestException("Missing required 'q' query parameter"));
        RemoteStockService remoteStockService = remoteStockServiceFactory.getForMarket(market);
        return remoteStockService.search(symbol, SEARCH_RESULT_LIMIT);
    }
}
